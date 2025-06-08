package nsk.nu.blackstone.Modules.Lifesteal.Listeners;

import nsk.nu.blackstone.Modules.Lifesteal.Event.RegisterLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealModule;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealService;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private static final NskLogger logger = NskLogger.get();
    // private LifestealModule module;
    // private LifestealService lifestealService;
    // private ClientService clientService;

    public void initialize(LifestealModule module, LifestealService service) {
        // this.module = module;
        // this.lifestealService = service;
        // this.clientService = module.getClientService();
        logger.log(LogLevels.FINE, "Lifesteal: PlayerJoin listener initialized.");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerJoinEvent(PlayerJoinEvent event) {

        RegisterLifestealEvent e = new RegisterLifestealEvent(event.getPlayer());
        PluginInstance.getInstance().getServer().getPluginManager().callEvent(e);

        if (e.isCancelled()) return;

    }

}
