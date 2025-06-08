package nsk.nu.blackstone.Modules.Lifesteal.Event;

import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SaveLifestealEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    private final UUID uuid;
    private final double health;
    private final double maxHealth;

    private boolean cancelled;

    public SaveLifestealEvent(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
    }

    public Player getPlayer() { return this.player; }
    public UUID getUuid() { return this.uuid; }

    public double getHealth() { return this.health; }
    public double getMaxHealth() { return this.maxHealth; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
