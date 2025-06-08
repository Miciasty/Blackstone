package nsk.nu.blackstone.Modules.Lifesteal.Listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Factory.BlackstoneDatabaseFactory;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.DLifesteal;
import nsk.nu.blackstone.Modules.Lifesteal.Event.RegisterLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealModule;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealService;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class RegisterLifestealListener implements Listener {

    private static final NskLogger logger = NskLogger.get();
    private LifestealModule module;
    private LifestealService lifestealService;
    private ClientService clientService;

    public void initialize(LifestealModule module, LifestealService service) {
        this.module = module;
        this.lifestealService = service;
        this.clientService = module.getClientService();
        logger.log(LogLevels.FINE, "Lifesteal: RegisterLifesteal listener initialized.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void RegisterLifestealEvent(RegisterLifestealEvent event) {

        Player player = event.getPlayer();

        if (clientService == null) {
            Component message = MiniMessage.miniMessage().deserialize(
                    """
                            Blackstone error 503!
                            Client Service temporarily unavailable.
                            Please contact the server administrator."""
            );
            player.kick(message);
            return;
        }

        UUID uuid = player.getUniqueId();
        double eventHealth = player.getHealth();
        double eventMaxHealth = player.getMaxHealth();

        double delta            = module.getConfig().getDouble("lifesteal.healthPerHeart"   , 2);
        double minHealth        = module.getConfig().getDouble("lifesteal.healthPerHeart"   , 2);
        double maxHealth        = module.getConfig().getDouble("lifesteal.maxHealth"        , 40);

        DScenario scenario = module.getScenario();

        clientService.getByClientAsync(uuid).thenAcceptAsync(clientDTO -> {

            if (clientDTO != null) {
                clientService.registerPlayer(uuid);
            }

            try {

                DScenario dScenario = module.getScenarioService().getByName(scenario.getName());

                try {
                    DLifesteal dLifesteal = lifestealService.getByClientAndScenario(uuid, scenario.getName());

                    if (dLifesteal != null) {
                        Bukkit.getScheduler().runTask(PluginInstance.getInstance(), () -> {
                            player.setMaxHealth( Math.max(minHealth, Math.min(dLifesteal.getMaxHealth(), maxHealth)) );
                            player.setHealth( Math.max(minHealth, Math.min(dLifesteal.getHealth(), maxHealth)) );
                            logger.log(LogLevels.INFO, "Lifesteal data loaded for player: " + player.getName() + " and scenario: " + scenario.getName() + ".");
                        });
                        return;
                    }

                    logger.log(LogLevels.INFO, "Lifesteal data not found for player: " + player.getName() + " and scenario: " + scenario.getName() + ".");

                    DLifesteal lifestealDTO = new DLifesteal();
                    lifestealDTO.setClient(clientDTO);
                    lifestealDTO.setScenario(dScenario);
                    lifestealDTO.setMaxHealth( Math.max(minHealth, Math.min(eventMaxHealth, maxHealth)) );
                    lifestealDTO.setHealth( Math.max(minHealth, Math.min(eventHealth, maxHealth)));
                    lifestealDTO.setKills(0);
                    lifestealDTO.setDeaths(0);
                    logger.log(LogLevels.INFO, "Creating new lifesteal data for player: " + player.getName() + " and scenario: " + scenario.getName() + ".");

                    try {
                        lifestealService.save(lifestealDTO);
                        logger.log(LogLevels.INFO, "New lifesteal data created for player: " + player.getName() + " and scenario: " + scenario.getName() + ".");
                    } catch (Exception ex) {
                        logger.log(LogLevels.SEVERE, "Failed to create new client for player: " + player.getName());
                        logger.log(LogLevels.SEVERE, ex.getMessage());
                        Component errorMsg = MiniMessage.miniMessage().deserialize(
                                """
                                        Blackstone error 504!
                                        Failed to create new client for player: <red>" + player.getName() + "</red>
                                        Please contact the server administrator."""
                        );
                        Bukkit.getServer().getScheduler().runTask(PluginInstance.getInstance(), () -> {
                            player.kick(errorMsg);
                        });
                        return;
                    }

                } catch (Exception ex) {
                    logger.log(LogLevels.SEVERE, "Failed to check if lifesteal data exists for player: " + player.getName() + " and scenario: " + scenario.getName());
                    logger.log(LogLevels.SEVERE, ex.getMessage());
                    return;
                }
            } catch (Exception ex) {
                logger.log(LogLevels.SEVERE, "Failed to check if scenario exists for player: " + player.getName());
                logger.log(LogLevels.SEVERE, ex.getMessage());
                return;
            }
        }, BlackstoneDatabaseFactory.getExecutor()).exceptionally(ex -> {
            logger.log(LogLevels.SEVERE, "Failed to check if client exists for player: " + player.getName());
            logger.log(LogLevels.SEVERE, ex.getMessage());
            return null;
        });

    }

}
