package nsk.nu.blackstone.Command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Service.Loader.ScenarioLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlackstoneCommand implements CommandExecutor, TabCompleter {

    private ScenarioLoader scenarioLoader;

    private String[] args;
    private CommandSender sender;

    public void initialize(ScenarioLoader scenarioLoader) {
        this.scenarioLoader = scenarioLoader;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("blackstone")) return true;

        if (    !sender.hasPermission("nsk.blackstone.admin") ||
                !sender.hasPermission("nsk.blackstone.admin.*") ) return true;

        if (args.length < 1) return true;

        this.args = args;
        this.sender = sender;

        switch (args[0].toLowerCase()) {
            case "scenario":
                this.scenario();
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (    !sender.hasPermission("nsk.blackstone.admin") ||
                !sender.hasPermission("nsk.blackstone.admin.*") ) return null;

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("scenario");
        }

        if (args.length == 2) {
            suggestions.add("load");
            suggestions.add("unload");
        }

        return suggestions;
    }

    private void scenario() {

        if (args.length < 3) {
            Component message = MiniMessage.miniMessage().deserialize("<blue>Currently loaded scenario: <white>" +
                    this.scenarioLoader.getActiveScenario().getName());
            sender.sendMessage(message);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "load":
                this.scenarioLoad();
                break;
            case "unload":
                this.scenarioUnload();
                break;
        }

    }

    private void scenarioLoad() {
        Component message = MiniMessage.miniMessage().deserialize("<green>Loaded scenario: <yellow>" + args[2]);
        this.scenarioUnloadActive()
                .thenCompose(v ->
                    this.scenarioLoader.loadScenario(args[2])
                )
                .thenRun(() -> sender.sendMessage( message ));
    }

    private void scenarioUnload() {
        Component message = MiniMessage.miniMessage().deserialize("<red>" + args[2] + "<yellow> scenario has been unloaded.");
        this.scenarioLoader.unloadScenario(args[2])
                .thenRun(() -> sender.sendMessage( message ));
    }

    private CompletableFuture<Void> scenarioUnloadActive() {
        DScenario activeScenario = this.scenarioLoader.getActiveScenario();
        if (activeScenario == null) return CompletableFuture.completedFuture(null);

        String name = activeScenario.getName();
        Component message = MiniMessage.miniMessage().deserialize("<red>" + name + "<yellow> scenario has been unloaded.");
        return this.scenarioLoader.unloadScenario( name )
                .thenRun(() -> sender.sendMessage( message ));
    }

}
