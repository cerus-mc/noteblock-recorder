package de.cerus.noteblockrecorder;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import de.cerus.noteblockrecorder.api.NoteblockRecorderApi;
import de.cerus.noteblockrecorder.command.NoteblockRecorderCommand;
import de.cerus.noteblockrecorder.config.impl.MessageConfig;
import de.cerus.noteblockrecorder.config.impl.PluginConfig;
import de.cerus.noteblockrecorder.listener.InventoryClickListener;
import de.cerus.noteblockrecorder.listener.NotePlayListener;
import de.cerus.noteblockrecorder.listener.PlayerInteractListener;
import de.cerus.noteblockrecorder.listener.PlayerQuitListener;
import de.cerus.noteblockrecorder.song.RecorderController;
import de.cerus.noteblockrecorder.song.SongController;
import de.cerus.noteblockrecorder.song.SongIndexes;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;

public class NoteblockRecorder extends JavaPlugin {

    private SongIndexes songIndexes;
    private PluginConfig pluginConfig;

    private boolean unsupported = false;

    @Override
    public void onEnable() {
        if (!getServer().getVersion().contains("(MC: 1.15")) {
            getLogger().severe(" ");
            getLogger().severe(" ");
            getLogger().severe("ERROR: Unsupported server version!");
            getLogger().severe("       Noteblock Recorder does only support 1.15 at the moment.");
            getLogger().severe("       Your server version: " + getServer().getVersion().split(" ")[2]
                    .replace(")", ""));
            getLogger().severe(" ");
            getLogger().severe(" ");

            unsupported = true;
            getPluginLoader().disablePlugin(this);
            return;
        }

        MessageConfig messageConfig;
        try {
            pluginConfig = new PluginConfig(this);
            pluginConfig.init();

            messageConfig = new MessageConfig(this);
            messageConfig.init();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        File songsFolder = new File(getDataFolder(), pluginConfig.songsFolder);
        songIndexes = SongIndexes.readDefault(songsFolder);
        if (songIndexes == null) {
            getLogger().severe("Failed to read song index file!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        songIndexes.reindexIfNeeded(songsFolder);

        SongController songController = new SongController(this, pluginConfig, messageConfig, pluginConfig, songIndexes);
        RecorderController recorderController = new RecorderController();

        BukkitCommandManager commandManager = new BukkitCommandManager(this);

        try {
            CommandReplacements replacements = commandManager.getCommandReplacements();
            pluginConfig.getPermissions().forEach(replacements::addReplacement);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        commandManager.getCommandCompletions().registerCompletion("songs", c -> songIndexes.getSongNames().values());
        commandManager.getCommandCompletions().registerCompletion("looking_at", c -> {
            Player player = c.getPlayer();
            if (player == null) {
                return Collections.emptyList();
            }

            RayTraceResult result = player.rayTraceBlocks(5, FluidCollisionMode.ALWAYS);
            if (result == null) {
                return Collections.emptyList();
            }

            Block hitBlock = result.getHitBlock();
            if (hitBlock == null) {
                return Collections.emptyList();
            }

            Location location = hitBlock.getLocation();
            return Collections.singletonList(location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ());
        });

        commandManager.registerCommand(new NoteblockRecorderCommand(pluginConfig, messageConfig, recorderController, songController));

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerInteractListener(recorderController), this);
        pluginManager.registerEvents(new NotePlayListener(recorderController), this);
        pluginManager.registerEvents(new InventoryClickListener(songController, pluginConfig, messageConfig), this);
        pluginManager.registerEvents(new PlayerQuitListener(recorderController), this);

        initializeApi(songController, songsFolder);
    }

    @Override
    public void onDisable() {
        if (unsupported) {
            return;
        }

        try {
            Method method = NoteblockRecorderApi.class.getDeclaredMethod("destroy");
            method.setAccessible(true);
            method.invoke(null);
        } catch (Exception ignored) {
        }

        File songsFolder = new File(getDataFolder(), pluginConfig.songsFolder);
        songIndexes.reindexIfNeeded(songsFolder);
    }

    private void initializeApi(SongController songController, File songFolder) {
        try {
            Method method = NoteblockRecorderApi.class.getDeclaredMethod("init", SongController.class, SongIndexes.class, File.class);
            method.setAccessible(true);
            method.invoke(null, songController, songIndexes, songFolder);
        } catch (Exception ignored) {
        }
    }
}
