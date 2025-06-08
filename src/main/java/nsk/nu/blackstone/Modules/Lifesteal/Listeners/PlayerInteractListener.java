package nsk.nu.blackstone.Modules.Lifesteal.Listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nsk.nu.blackstone.Modules.Lifesteal.Event.UseLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealModule;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealService;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class PlayerInteractListener implements Listener {

    private static final NskLogger logger = NskLogger.get();
    private static final Logger log = LoggerFactory.getLogger(PlayerInteractListener.class);

    private LifestealModule module;

    public void initialize(LifestealModule module, LifestealService service) {
        this.module = module;
        logger.log(LogLevels.FINE, "Lifesteal: PlayerInteract listener initialized.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void PlayerInteractEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR) return;
        if (action == Action.LEFT_CLICK_BLOCK) return;
        if (action == Action.PHYSICAL) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        ItemStack usedItem = null;
        boolean isMain = false;

        if (isLifestealItem(mainHand)) {
            usedItem = mainHand;
            isMain = true;
        } else if (isLifestealItem(offHand)) {
            usedItem = offHand;
            isMain = false;
        } 

        if (usedItem == null) return;

        UseLifestealEvent e = new UseLifestealEvent(player, usedItem);
        PluginInstance.getInstance().getServer().getPluginManager().callEvent(e);

        if (e.isCancelled()) return;

        usedItem.setAmount(usedItem.getAmount() - 1);
        if (usedItem.getAmount() <= 0) {
            if (isMain) {
                player.getInventory().setItemInMainHand(null);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
        }

    }

    private boolean isLifestealItem(ItemStack item) {

        if (item == null || item.getType() == Material.AIR) return false;

        ItemStack original = module.getLifestealItem();
        ItemMeta originalMeta = original.getItemMeta();

        if (original.getType() != item.getType()) return false;

        Component expectedName = originalMeta.displayName();
        Component actualName = item.getItemMeta().displayName();
        String expectedPlain = expectedName != null ? PlainTextComponentSerializer.plainText().serialize(expectedName) : null;
        String actualPlain = actualName != null ? PlainTextComponentSerializer.plainText().serialize(actualName) : null;

        if ( !Objects.equals(expectedPlain, actualPlain) ) return false;

        List<Component> originalLore = originalMeta.lore();
        List<Component> itemLore = item.getItemMeta().lore();

        if (!Objects.equals(originalLore, itemLore)) return false;

        if (module.getConfig().contains("item.model_data")) {
            Integer expectedModel = originalMeta.hasCustomModelData() ? originalMeta.getCustomModelData() : null;
            Integer actualModel = item.getItemMeta().hasCustomModelData() ? item.getItemMeta().getCustomModelData() : null;
            return Objects.equals(expectedModel, actualModel);
        }

        return true;
    }

}
