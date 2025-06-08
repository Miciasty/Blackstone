package nsk.nu.blackstone;

public class PluginInstance {

    private static Plugin instance;
    public static Plugin getInstance() {
        return instance;
    }

    public static void setInstance(Plugin p) {
        instance = p;
    }

}