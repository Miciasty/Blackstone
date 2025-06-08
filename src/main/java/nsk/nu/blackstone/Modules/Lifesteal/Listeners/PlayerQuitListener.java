package nsk.nu.blackstone.Modules.Lifesteal.Listeners;

import nsk.nu.blackstone.Modules.Lifesteal.Event.SaveLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealModule;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealService;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private static final NskLogger logger = NskLogger.get();
    // private LifestealModule module;
    // private LifestealService lifestealService;
    private ClientService clientService;

    public void initialize(LifestealModule module, LifestealService service) {
        // this.module = module;
        // this.lifestealService = service;
        this.clientService = module.getClientService();
        logger.log(LogLevels.FINE, "Lifesteal: PlayerQuit listener initialized.");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerQuitEvent(PlayerQuitEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();

        SaveLifestealEvent e = new SaveLifestealEvent(event.getPlayer());
        PluginInstance.getInstance().getServer().getPluginManager().callEvent(e);

        if (e.isCancelled()) return;

    }

}
