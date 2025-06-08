package nsk.nu.blackstone.Event;

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
    private ClientService service;

    public void initialize(ClientService service) {
        this.service = service;
        logger.log(LogLevels.FINE, "Blackstone: PlayerJoin listener initialized.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerQuitEvent(PlayerQuitEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();

        service.unregisterPlayer(uuid);

    }
}
