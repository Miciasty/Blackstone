package nsk.nu.dev.Logs;

import java.util.logging.Level;

public class LogLevels extends Level {

    public static final Level CRITICAL  = new LogLevels("CRITICAL", 9999);
    public static final Level DEV       = new LogLevels("DEV", 9998);
    public static final Level LAMBDA    = new LogLevels("LAMBDA", 9997);

    protected LogLevels(String name, int value) {
        super(name, value);
    }

}
