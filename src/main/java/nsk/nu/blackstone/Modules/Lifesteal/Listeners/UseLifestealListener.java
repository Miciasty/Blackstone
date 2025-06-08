package nsk.nu.blackstone.Modules.Lifesteal.Listeners;

import nsk.nu.blackstone.Modules.Lifesteal.Event.SaveLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.Event.UseLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealModule;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealService;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class UseLifestealListener implements Listener {

    private static final NskLogger logger = NskLogger.get();

    private LifestealModule module;

    public void initialize(LifestealModule module, LifestealService service) {
        this.module = module;
        logger.log(LogLevels.FINE, "Lifesteal: UseLifesteal listener initialized.");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void UseLifestealEvent(UseLifestealEvent event) {

        Player player = event.getPlayer();

        double delta            = module.getConfig().getDouble("lifesteal.healthPerHeart"   , 2);
        double minHealth        = module.getConfig().getDouble("lifesteal.healthPerHeart"   , 2);
        double maxHealth        = module.getConfig().getDouble("lifesteal.maxHealth"        , 40);

        if (player.getMaxHealth() + delta > maxHealth) {
            logger.log(LogLevels.FINE, "Player's health cannot be increased by " + delta + " as it would exceed the maximum health limit of " + maxHealth + ".");
            event.setCancelled(true);
            return;
        }

        player.setMaxHealth(player.getMaxHealth() + delta);
        player.setHealth(Math.min(player.getHealth() + delta, maxHealth));
        logger.log(LogLevels.FINE, "Player's health increased by " + delta + " to " + player.getHealth() + ".");

        SaveLifestealEvent e = new SaveLifestealEvent(player);
        PluginInstance.getInstance().getServer().getPluginManager().callEvent(e);

    }

}
