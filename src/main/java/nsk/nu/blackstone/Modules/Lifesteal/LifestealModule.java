package nsk.nu.blackstone.Modules.Lifesteal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.nu.blackstone.Abstract.AbstractBlackstoneModule;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.DLifesteal;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.Lifesteal;
import nsk.nu.blackstone.Modules.Lifesteal.Event.RegisterLifestealEvent;
import nsk.nu.blackstone.Modules.Lifesteal.Listeners.*;
import nsk.nu.blackstone.Plugin;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.blackstone.Service.ScenarioService;
import nsk.nu.dev.Configuration.HibernateUtil;
import nsk.nu.dev.Logs.LogLevels;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LifestealModule extends AbstractBlackstoneModule<Lifesteal, DLifesteal, LifestealService, LifestealRepository> {

    public LifestealModule(ScenarioService scenarioService, ClientService clientService, String name) {
        super(scenarioService, clientService, name);
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    @Override
    protected LifestealRepository createRepository() {
        return new LifestealRepository( HibernateUtil.getSessionFactory() );
    }

    @Override
    protected LifestealService createService(LifestealRepository repository) {
        return new LifestealService(repository);
    }

    @Override
    protected void registerEvents() {
        Plugin i = PluginInstance.getInstance();

        RegisterLifestealListener   rll = new RegisterLifestealListener();
        i.getServer().getPluginManager().registerEvents(rll, i);    rll.initialize(this, service);   registeredListeners.add(rll);
        SaveLifestealListener       sll = new SaveLifestealListener();
        i.getServer().getPluginManager().registerEvents(sll, i);    sll.initialize(this, service);   registeredListeners.add(sll);
        UseLifestealListener        ull = new UseLifestealListener();
        i.getServer().getPluginManager().registerEvents(ull, i);    ull.initialize(this, service);   registeredListeners.add(ull);
        PlayerInteractListener      pil = new PlayerInteractListener();
        i.getServer().getPluginManager().registerEvents(pil, i);    pil.initialize(this, service);   registeredListeners.add(pil);
        PlayerJoinListener          pjl = new PlayerJoinListener();
        i.getServer().getPluginManager().registerEvents(pjl, i);    pjl.initialize(this, service);   registeredListeners.add(pjl);
        PlayerQuitListener          pqj = new PlayerQuitListener();
        i.getServer().getPluginManager().registerEvents(pqj, i);    pqj.initialize(this, service);   registeredListeners.add(pqj);
        PlayerDeathListener         pdl = new PlayerDeathListener();
        i.getServer().getPluginManager().registerEvents(pdl, i);    pdl.initialize(this, service);   registeredListeners.add(pdl);

        logger.log(LogLevels.FINE, "Lifesteal module events are now registered.");
    }

    @Override
    public void loadConfig() {
        if (config == null) initializeConfig();
    }

    @Override
    protected void registerPlayerEvent(Player player) {
        RegisterLifestealEvent event = new RegisterLifestealEvent(player);
        PluginInstance.getInstance().getServer().getPluginManager().callEvent(event);
    }

    public ItemStack getLifestealItem() {

        String material = this.getConfig().getString("item.material", "PAPER");

        ItemStack item = new ItemStack(Material.valueOf(material));
        ItemMeta meta = item.getItemMeta();

        Component name = MiniMessage.miniMessage().deserialize( this.getConfig().getString(
                "item.name",
                "<red>Heart"
        ));
        meta.displayName(name);

        List<String> defaultLore = new ArrayList<>();
        defaultLore.add("Someone's heart");
        defaultLore.add("");
        defaultLore.add("<yellow><bold>[RMB]<reset> to gain");
        defaultLore.add("an additional heart.");

        List<String> lore = this.getConfig().contains("item.lore")
                ? this.getConfig().getStringList("item.lore")
                : defaultLore;
        List<Component> componentLore = lore.stream()
                .map(line -> MiniMessage.miniMessage().deserialize(line))
                .toList();
        meta.lore(componentLore);

        if (this.getConfig().contains("item.model_data")) {
            meta.setCustomModelData(this.getConfig().getInt("item.model_data", 1537));
        }

        // boolean stackable = module.getConfig().getBoolean("item.stackable", false);
        // Have to add logic to prevent stacking this item.

        item.setItemMeta(meta);

        return item;
    }

}
