package nsk.nu.blackstone.Modules.Lifesteal.Listeners;

import nsk.nu.blackstone.Factory.BlackstoneDefaultFactory;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.DLifesteal;
import nsk.nu.blackstone.Modules.Lifesteal.Enums.LifestealType;
import nsk.nu.blackstone.Modules.Lifesteal.Enums.ThunderPosition;
import nsk.nu.blackstone.Modules.Lifesteal.Enums.ThunderType;
import nsk.nu.blackstone.Modules.Lifesteal.Event.RegisterLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealModule;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealService;
import nsk.nu.blackstone.Plugin;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.blackstone.Service.ScenarioService;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private static final NskLogger logger = NskLogger.get();
    private LifestealModule module;
    private LifestealService lifestealService;

    private ScenarioService scenarioService;
    private ClientService clientService;

    private boolean areClients = true;

    private Location deathLocation;

    public void initialize(LifestealModule module, LifestealService service) {
        this.module = module;
        this.lifestealService = service;
        this.scenarioService = module.getScenarioService();
        this.clientService = module.getClientService();
        logger.log(LogLevels.FINE, "Lifesteal: PlayerDeath listener initialized.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerDeathEvent(PlayerDeathEvent event) {
        if (!module.isEnabled() || module == null) return;

        Player victim = event.getPlayer();
        UUID vUUID = victim.getUniqueId();

        if (!clientService.isRegistered(vUUID)) {
            areClients = false;
            RegisterLifestealEvent registerLifestealEvent = new RegisterLifestealEvent(victim);
            Bukkit.getPluginManager().callEvent(registerLifestealEvent);

            logger.log(LogLevels.FINE, "Registering new client for player: " + victim.getName() + ".");
        }

        this.deathLocation = victim.getLocation();


        if (event.getEntity() == victim) {
            this.playersOwnDeath(victim);
            return;
        }

        this.playerMurdered(victim, event.getEntity());


    }

    private void playersOwnDeath(Player victim) {

        UUID vUUID = victim.getUniqueId();

        Plugin instance = PluginInstance.getInstance();
        String scenarioName = module.getScenario().getName();

        final int delta       = module.getConfig().getInt("healthPerHeart");
        final int minHealth   = module.getConfig().getInt("healthPerHeart");
        final int maxHealth   = module.getConfig().getInt("maxHealth");

        lifestealService.getByClientAndScenarioAsync(vUUID, scenarioName).thenAcceptAsync(victimLifesteal -> {

            if (!(victimLifesteal.getMaxHealth() - delta < minHealth)) {
                victimLifesteal.setMaxHealth(victimLifesteal.getMaxHealth() - delta);
                victimLifesteal.setHealth(Math.max(victimLifesteal.getHealth() - delta, minHealth));
                logger.log(LogLevels.FINE, "Victim health reduced by " + delta + " to " + victimLifesteal.getHealth() + ".");
            } else {
                Bukkit.getScheduler().runTask(instance, () -> {
                    victim.setGameMode(GameMode.SPECTATOR);
                });
                victimLifesteal.setMaxHealth(minHealth);
                victimLifesteal.setHealth(minHealth);
                logger.log(LogLevels.FINE, "Victim health reduced to " + victimLifesteal.getHealth() + ".");
            }

            victimLifesteal.setDeaths(victimLifesteal.getDeaths() + 1);

            try {
                lifestealService.save(victimLifesteal);
                logger.log(LogLevels.FINE, "Victim lifesteal data saved for player: " + victim.getUniqueId());
            } catch (Exception ex) {
                logger.log(LogLevels.SEVERE, "Failed to save victim lifesteal data for player: " + victim.getUniqueId());
                logger.log(LogLevels.SEVERE, ex.getMessage());
                return;
            }

            Bukkit.getScheduler().runTask(instance, () -> {
                victim.setMaxHealth(victimLifesteal.getMaxHealth());
                victim.setHealth(victimLifesteal.getHealth());
            });

            LifestealType lifestealType;
            try {
                lifestealType = LifestealType.valueOf(module.getConfig().getString("lifestealType", "DROP").toUpperCase());
            } catch (Exception ex) {
                lifestealType = LifestealType.SET;
            }

            if (lifestealType == LifestealType.DROP) {
                this.spawnLightningAndDrop(deathLocation);
            }

        }, BlackstoneDefaultFactory.getExecutor()).exceptionally(ex -> {
            logger.log(LogLevels.SEVERE, "Failed to load lifesteal data for victim: " + victim.getName() + "!");
            logger.log(LogLevels.SEVERE, ex.getMessage());
            return null;
        });

    }

    private void playerMurdered(Player victim, Player killer) {

        if (killer == null) return;
        if (killer.equals(victim)) return;

        UUID kUUID = killer.getUniqueId();
        UUID vUUID = victim.getUniqueId();

        if (!clientService.isRegistered(kUUID)) {
            areClients = false;
            RegisterLifestealEvent registerLifestealEvent = new RegisterLifestealEvent(killer);
            Bukkit.getPluginManager().callEvent(registerLifestealEvent);

            logger.log(LogLevels.FINE, "Registering new client for player: " + killer.getName() + ".");
        }
        if (!areClients) {
            return;
        }

        Plugin instance = PluginInstance.getInstance();
        String scenarioName = module.getScenario().getName();

        final int delta       = module.getConfig().getInt("healthPerHeart");
        final int minHealth   = module.getConfig().getInt("healthPerHeart");
        final int maxHealth   = module.getConfig().getInt("maxHealth");

        lifestealService.getByClientAndScenarioAsync(kUUID, scenarioName).thenAcceptAsync(killerLifesteal -> {

            try {
                DLifesteal victimLifesteal = lifestealService.getByClientAndScenario(vUUID, scenarioName);

                if (victimLifesteal == null) {
                    logger.log(LogLevels.FINE, "Victim lifesteal data not found for player: " + victim.getName() + ".");
                    RegisterLifestealEvent e = new RegisterLifestealEvent(victim);
                    PluginInstance.getInstance().getServer().getPluginManager().callEvent(e);
                    return;
                }

                if (!(victimLifesteal.getMaxHealth() - delta < minHealth)) {
                    victimLifesteal.setMaxHealth(victimLifesteal.getMaxHealth() - delta);
                    victimLifesteal.setHealth(Math.max(victimLifesteal.getHealth() - delta, minHealth));
                    logger.log(LogLevels.FINE, "Victim health reduced by " + delta + " to " + victimLifesteal.getHealth() + ".");
                } else {
                    Bukkit.getScheduler().runTask(instance, () -> {
                        victim.setGameMode(GameMode.SPECTATOR);
                    });
                    victimLifesteal.setMaxHealth(minHealth);
                    victimLifesteal.setHealth(minHealth);
                    logger.log(LogLevels.FINE, "Victim health reduced to " + victimLifesteal.getHealth() + ".");
                }

                victimLifesteal.setDeaths(victimLifesteal.getDeaths() + 1);

                logger.log(LogLevels.FINE, "Killer health increased by " + delta + " to " + killerLifesteal.getHealth() + ".");

                try {
                    lifestealService.save(victimLifesteal);
                    logger.log(LogLevels.FINE, "Victim lifesteal data saved for player: " + victim.getUniqueId());
                } catch (Exception ex) {
                    logger.log(LogLevels.SEVERE, "Failed to save victim lifesteal data for player: " + victim.getUniqueId());
                    logger.log(LogLevels.SEVERE, ex.getMessage());
                    return;
                }

                Bukkit.getScheduler().runTask(instance, () -> {
                    victim.setMaxHealth(victimLifesteal.getMaxHealth());
                    victim.setHealth(victimLifesteal.getHealth());
                });

            } catch (Exception ex) {
                logger.log(LogLevels.SEVERE, "Failed to load lifesteal data for victim: " + victim.getName() + "!");
                logger.log(LogLevels.SEVERE, ex.getMessage());
                return;
            }

            LifestealType lifestealType;
            try {
                lifestealType = LifestealType.valueOf(module.getConfig().getString("lifestealType", "DROP").toUpperCase());
            } catch (Exception ex) {
                lifestealType = LifestealType.SET;
            }

            if (lifestealType == LifestealType.DROP) {
                this.spawnLightningAndDrop(deathLocation);
            } else {
                this.castThunder(deathLocation);
                this.giveLifestealItem(killer);
            }

            killerLifesteal.setKills(killerLifesteal.getKills() + 1);

            killerLifesteal.setMaxHealth(Math.min(killerLifesteal.getMaxHealth() + delta, maxHealth));
            killerLifesteal.setHealth(Math.min(killerLifesteal.getHealth() + delta, killerLifesteal.getMaxHealth()));

            try {
                lifestealService.save(killerLifesteal);
                logger.log(LogLevels.FINE, "Killer lifesteal data saved for player: " + killer.getUniqueId());
            } catch (Exception ex) {
                logger.log(LogLevels.SEVERE, "Failed to save killer lifesteal data for player: " + killer.getUniqueId());
                logger.log(LogLevels.SEVERE, ex.getMessage());
                return;
            }

            Bukkit.getScheduler().runTask(instance, () -> {
                killer.setMaxHealth(killerLifesteal.getMaxHealth());
                killer.setHealth(killerLifesteal.getHealth());
            });

            logger.log(LogLevels.FINE, "Lifesteal data saved for victim: " + victim.getName() + ", killer: " + killer.getName() + ".");

        }, BlackstoneDefaultFactory.getExecutor()).exceptionally(ex -> {
            logger.log(LogLevels.SEVERE, "Failed to load lifesteal data for killer: " + killer.getName() + "!");
            logger.log(LogLevels.SEVERE, ex.getMessage());
            return null;
        });

    }

    private void spawnLightningAndDrop(Location location) {
        this.castThunder(location);
        Bukkit.getScheduler().runTaskLater(PluginInstance.getInstance(), () -> {
            Location blockLoc = location.clone().subtract(0, 1, 0);
            if (blockLoc.getBlock().getType() == Material.FIRE) {
                blockLoc.getBlock().setType(Material.AIR);
            }
            this.giveLifestealItem(location);
        }, 15L);
    }

    private void castThunder(Location location) {
        if (!module.getConfig().getBoolean("lighting.enabled", false)) return;

        ThunderType type;
        try {
            type = ThunderType.valueOf(module.getConfig().getString("lighting.type", "DAMAGE").toUpperCase());
        } catch (Exception ex) {
            type = ThunderType.DAMAGE;
        }

        ThunderPosition position;
        try {
            position = ThunderPosition.valueOf(module.getConfig().getString("lighting.position", "HIGHEST").toUpperCase());
        } catch (Exception ex) {
            position = ThunderPosition.HIGHEST;
        }

        Location strikeLocation;
        switch (position) {
            case LOWEST:
                strikeLocation = getGroundLocation(location);
                break;
            case HIGHEST:
            default:
                strikeLocation = getHighestLocation(location);
                break;
        }

        switch (type) {
            case VISUAL:
                PluginInstance.getInstance().getServer().getScheduler().runTask(PluginInstance.getInstance(), () -> {
                    location.getWorld().strikeLightningEffect(strikeLocation);
                });
                break;
            case DAMAGE:
            default:
                PluginInstance.getInstance().getServer().getScheduler().runTask(PluginInstance.getInstance(), () -> {
                    location.getWorld().strikeLightning(strikeLocation);
                });
                break;
        }

    }

    private Location getHighestLocation(Location base) {
        int x = base.getBlockX();
        int z = base.getBlockZ();
        int highestY = base.getWorld().getHighestBlockYAt(x, z);
        return new Location(base.getWorld(), x + 0.5, highestY, z + 0.5);
    }

    private Location getGroundLocation(Location base) {
        int x = base.getBlockX();
        int z = base.getBlockZ();
        int y = base.getBlockY();
        while (y > 0 && base.getWorld().getBlockAt(x, y, z).isPassable()) {
            y--;
        }
        return new Location(base.getWorld(), x + 0.5, y + 1, z + 0.5);
    }

    private void giveLifestealItem(Player player) {
        ItemStack item = module.getLifestealItem();
        int slot = hasFreeSpace(player.getInventory());

        if ( slot == -1 ) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            logger.log(LogLevels.INFO, player.getName() + " had no inventory space and dropped the lifesteal item.");
            return;
        }

        player.getInventory().setItem(slot, item);
        player.updateInventory();
        logger.log(LogLevels.INFO, player.getName() + " received a lifesteal item.");
    }

    private void giveLifestealItem(Location location) {
        ItemStack item = module.getLifestealItem();
        location.getWorld().dropItemNaturally(location, item);
        logger.log(LogLevels.INFO, "Someones heart dropped at the: world:" + location.getWorld() +  ", x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ() + ".");
    }

    private int hasFreeSpace(PlayerInventory playerInventory) {
        ItemStack[] contents = playerInventory.getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null || contents[i].getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }

}
