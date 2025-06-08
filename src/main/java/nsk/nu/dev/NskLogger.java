package nsk.nu.dev;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.nu.blackstone.Plugin;
import nsk.nu.dev.Logs.LogLevels;
import org.bukkit.Bukkit;

import java.util.logging.*;

public class NskLogger extends Logger {

    /* --- --- --- --- --- --- --- --- --- --- */

    private static NskLogger instance;

    public static void set(Plugin plugin) {
        NskLogger.instance = new NskLogger(plugin);
    }
    public static NskLogger get() {
        return NskLogger.instance;
    }

    /* --- --- --- --- --- --- --- --- --- --- */

    public NskLogger(Plugin plugin) {
        super(plugin.getName(), null);
        setParent(Bukkit.getLogger());
        setLevel(Level.ALL);

        setUseParentHandlers(false);

        for (Handler handler : getHandlers()) {
            removeHandler(handler);
        }
        NskLoggerHandler enhancedHandler = new NskLoggerHandler();
        enhancedHandler.setFormatter(new SimpleFormatter());
        addHandler(enhancedHandler);
    }

    private class NskLoggerHandler extends Handler {
        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record)) return;

            String prefix = "Blackstone";
            String lambdaPrefix = "λ";
            String nskPrefix = "nsk.";

            String message = getFormatter().formatMessage(record);
            Level level = record.getLevel();

            Component casual = MiniMessage.miniMessage().deserialize("<gradient:#1f8eb2:#2dccff>["+ prefix +"]</gradient> " + message);

            if (level.equals(Level.SEVERE)) {
                Component severe = MiniMessage.miniMessage().deserialize("<gradient:#b24242:#ff5f5f>[" + prefix + "]</gradient> <#ffafaf>" + message);
                Bukkit.getConsoleSender().sendMessage(severe);

            } else if (level.equals(Level.WARNING)) {
                Component warning = MiniMessage.miniMessage().deserialize("<gradient:#b28724:#ffc234>[" + prefix + "]</gradient> <#ffe099>" + message);
                Bukkit.getConsoleSender().sendMessage(warning);

            } else if (level.equals(Level.FINE)) {
                Component fine = MiniMessage.miniMessage().deserialize("<gradient:#3ca800:#56f000>[" + prefix + "]</gradient> <#aaf77f>" + message);
                Bukkit.getConsoleSender().sendMessage(fine);

            } else if (level.equals(Level.CONFIG)) {
                Component dev = MiniMessage.miniMessage().deserialize("<gradient:#b28724:#ffc234>[" + prefix + "]</gradient><gradient:#1f8eb2:#2dccff> [Config] </gradient> <#ffe099>" + message);
                Bukkit.getConsoleSender().sendMessage(dev);

            } else if (level.equals(LogLevels.DEV)) {
                Component dev = MiniMessage.miniMessage().deserialize("<gradient:#9863E7:#4498DB>[" + nskPrefix + "dev" + "] </gradient><#63E798>" + message);
                Bukkit.getConsoleSender().sendMessage(dev);

            } else if (level.equals(LogLevels.LAMBDA)) {
                Component dev = MiniMessage.miniMessage().deserialize("<gradient:#ffc234:#ffc234>[" + lambdaPrefix + "] </gradient><#ffe099>" + message);
                Bukkit.getConsoleSender().sendMessage(dev);

            } else {
                Bukkit.getConsoleSender().sendMessage(casual);
            }

        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}

    }
    
}
