package nsk.nu.blackstone.Modules.Lifesteal.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegisterLifestealEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled;

    public RegisterLifestealEvent(Player player) { this.player = player; }

    public Player getPlayer() { return this.player; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }

}
