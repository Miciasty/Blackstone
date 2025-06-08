package nsk.nu.blackstone.Event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.nu.blackstone.Entity.DTO.DClient;
import nsk.nu.blackstone.Factory.BlackstoneDefaultFactory;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private static final NskLogger logger = NskLogger.get();
    private ClientService service;

    public void initialize(ClientService service) {
        this.service = service;
        logger.log(LogLevels.FINE, "Blackstone: PlayerJoin listener initialized.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerJoinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (service == null) {
            Component message = MiniMessage.miniMessage().deserialize(
                    """
                            Blackstone error 503!
                            Client Service temporarily unavailable.
                            Please contact the server administrator."""
            );
            player.kick(message);
            return;
        }

        service.getByClientAsync(player.getUniqueId()).thenAcceptAsync(clientDTO -> {
            if (clientDTO != null) {
                logger.log(LogLevels.INFO, "Client " + player.getName() + " joined the server.");
                service.registerPlayer(uuid);
                return;
            };

            logger.log(LogLevels.INFO, "Client does not exist for player: " + player.getName());
            DClient newClient = new DClient();
            newClient.setUuid(player.getUniqueId());

            logger.log(LogLevels.INFO, "Creating new client for player: " + player.getName());

            try {
                service.save(newClient);
                logger.log(LogLevels.INFO, "New client created for player: " + player.getName());
                service.registerPlayer(uuid);
            } catch (Exception e) {
                logger.log(LogLevels.SEVERE, "Failed to create new client for player: " + player.getName());
                logger.log(LogLevels.SEVERE, e.getMessage());
                Component errorMsg = MiniMessage.miniMessage().deserialize(
                        """
                                Blackstone error 504!
                                Failed to create new client for player: <red>" + player.getName() + "</red>
                                Please contact the server administrator."""
                );
                PluginInstance.getInstance().getServer().getScheduler().runTask(PluginInstance.getInstance(), () -> {
                    player.kick(errorMsg);
                });
                return;
            }

        }, BlackstoneDefaultFactory.getExecutor()).exceptionally(ex -> {
            logger.log(LogLevels.SEVERE, "Failed to check if client exists for player: " + player.getName());
            logger.log(LogLevels.SEVERE, ex.getMessage());
            return null;
        });

    }

}
