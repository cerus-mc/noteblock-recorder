package de.cerus.noteblockrecorder.song;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;

public class Song {

    private String name;
    private UUID author;
    private String authorName;
    private Set<Sound> sounds;

    public Song(String name, UUID author, Set<Sound> sounds) {
        this.name = name;
        this.author = author;
        this.authorName = (Bukkit.getOfflinePlayer(author).hasPlayedBefore()
                ? Bukkit.getOfflinePlayer(author).getName() : author.toString());
        this.sounds = sounds;
    }

    public BukkitRunnable toRunnable(Player player) {
        return new BukkitRunnable() {
            private final int maxDelay = getLengthInTicks();
            private int current = 0;

            @Override
            public void run() {
                if (maxDelay <= 0) {
                    cancel();
                    return;
                }
                if (current > maxDelay) {
                    cancel();
                    return;
                }

                for (Sound sound : sounds) {
                    if (sound.getDelay() != current) {
                        continue;
                    }

                    sound.play(player);
                }

                int seconds = (maxDelay - current) / 20;
                int minutes = seconds / 60;
                if (minutes > 0) {
                    seconds = seconds % 60;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d" + String.format("%02d:%02d", minutes, seconds)));

                current++;
            }
        };
    }

    public int getLengthInTicks() {
        return sounds.stream()
                .mapToInt(Sound::getDelay)
                .max()
                .orElse(0);
    }

    public String getName() {
        return name;
    }

    public UUID getAuthor() {
        return author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Set<Sound> getSounds() {
        return sounds;
    }

}
