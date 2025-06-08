package nsk.nu.blackstone.Modules.Lifesteal.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UseLifestealEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack item;

    private boolean cancelled;
    public UseLifestealEvent(Player player, ItemStack item) { this.player = player; this.item = item; }

    public Player getPlayer() { return this.player; }
    public ItemStack getItem() { return this.item; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
