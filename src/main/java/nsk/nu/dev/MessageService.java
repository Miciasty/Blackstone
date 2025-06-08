package nsk.nu.dev;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class MessageService {

    private final Player player;

    private Component title;
    private Component subtitle;
    private Component chatMessage;

    public MessageService(Player player) {
        this.player = player;
    }

    public MessageService(Player player, String chatMessage) {
        this.player = player;
        this.chatMessage = MiniMessage.miniMessage().deserialize(chatMessage, Tags.getGradient(), Tags.getStandard());
    }

    // --- --- --- --- --- --- --- --- --- --- //

    public void setTitle(String title) {
        this.title = MiniMessage.miniMessage().deserialize(title, Tags.getGradient(), Tags.getStandard());
    }
    public void setTitle(String title, String subtitle) {
        this.title = MiniMessage.miniMessage().deserialize(title, Tags.getGradient(), Tags.getStandard());
        this.subtitle = MiniMessage.miniMessage().deserialize(subtitle, Tags.getGradient(), Tags.getStandard());
    }
    public Component getTitle() { return this.title; }

    public void setSubtitle(String subtitle) {
        this.subtitle = MiniMessage.miniMessage().deserialize(subtitle, Tags.getGradient(), Tags.getStandard());
    }
    public Component getSubtitle() { return this.subtitle; }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = MiniMessage.miniMessage().deserialize(chatMessage, Tags.getGradient(), Tags.getStandard());
    }
    public Component getChatMessage() { return this.chatMessage; }

    // --- --- --- --- --- --- --- --- --- --- //

    public void sendChatMessage() {
        player.sendMessage(this.chatMessage);
    }

    public void sendTitle(Long fadeIn, Long stay, Long fadeOut) {
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn),     // FadeIn
                Duration.ofMillis(stay),    // Stay
                Duration.ofMillis(fadeOut));   // FadeOut

        Title title = Title.title(this.title, this.subtitle, times);

        player.showTitle(title);
    }


}
