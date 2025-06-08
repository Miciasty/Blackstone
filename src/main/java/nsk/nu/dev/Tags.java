package nsk.nu.dev;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import nsk.nu.dev.Configuration.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class Tags {

    public static TagResolver getStandard() {

        Map<String, String> tags = getTags();
        TagResolver.Builder builder = TagResolver.builder();

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.resolver(Placeholder.parsed(key, "<"+value+">"));

            key = "-" + key;
            value = "</" + value + ">";

            builder.resolver(Placeholder.parsed(key, value));
        }

        return builder.build();

        /*return TagResolver.resolver(
                Placeholder.parsed("primary",   "<#9863E7>"),
                Placeholder.parsed("secondary", "<#4498DB>"),
                Placeholder.parsed("ascent",    "<#63E798>"),
                Placeholder.parsed("ascent2",   "<#FDDE6D>"),

                Placeholder.parsed("-primary",      "</#9863E7>"),
                Placeholder.parsed("-secondary",    "</#4498DB>"),
                Placeholder.parsed("-ascent",       "</#63E798>"),
                Placeholder.parsed("-ascent2",      "</#FDDE6D>")
        );*/
    }

    public static TagResolver getGradient() {

        Map<String, String> tags = getTags();
        TagResolver.Builder builder = TagResolver.builder();

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String key = entry.getKey();
            key = "g" + key;

            builder.resolver(Placeholder.parsed(key, entry.getValue()));
        }

        return builder.build();

        /*return TagResolver.resolver(
                Placeholder.parsed("gprimary",   "#9863E7"),
                Placeholder.parsed("gsecondary", "#4498DB"),
                Placeholder.parsed("gascent",    "#63E798"),
                Placeholder.parsed("gascent2",   "#FDDE6D")
        );*/
    }

    private static Map<String, String> getTags() {

        FileConfiguration tagsConfig = Config.getTags();
        ConfigurationSection tagsSection = tagsConfig.getConfigurationSection("Tags");

        Map<String, String> tags = new HashMap<String, String>();
        if (tagsSection != null) {
            for (String tag : tagsSection.getKeys(false)) {
                String color = tagsSection.getString(tag, "#FF0000").toUpperCase();

                if (color.charAt(0) != '#') {
                    color = "#" + color;
                }

                tags.put(tag, color);
            }
        }

        return tags;

    }

}
