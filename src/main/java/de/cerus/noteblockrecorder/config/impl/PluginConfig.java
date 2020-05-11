package de.cerus.noteblockrecorder.config.impl;

import de.cerus.noteblockrecorder.NoteblockRecorder;
import de.cerus.noteblockrecorder.config.Config;
import de.cerus.noteblockrecorder.config.annotations.ConfigEntry;
import de.cerus.noteblockrecorder.config.annotations.ConfigSettings;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@ConfigSettings(header = "Noteblock Recorder Config")
public class PluginConfig extends Config {

    @ConfigEntry(key = "songs-per-user")
    public int songsPerUser = -1;

    @ConfigEntry(key = "songs-folder")
    public String songsFolder = "./songs";

    @ConfigEntry(key = "permission.create-songs")
    public String permissionCreateSongs = "nbr.songs.create";

    @ConfigEntry(key = "permission.use-command")
    public String permissionUseCommand = "nbr.command";

    @ConfigEntry(key = "permission.play-songs")
    public String permissionPlaySongs = "nbr.songs.play";

    @ConfigEntry(key = "permission.browse-songs")
    public String permissionBrowseSongs = "nbr.songs.browse";

    @ConfigEntry(key = "permission.manage-songs")
    public String permissionManageSongs = "nbr.songs.manage";

    @ConfigEntry(key = "permission.limit-bypass")
    public String permissionLimitBypass = "nbr.bypass-limit";

    public PluginConfig(NoteblockRecorder plugin) {
        super(new File(plugin.getDataFolder(), "config.yml"));
    }

    public Map<String, String> getPermissions() throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        for (Field field : getClass().getDeclaredFields()) {
            if(!field.isAnnotationPresent(ConfigEntry.class)) {
                continue;
            }
            if(field.getType() != String.class) {
                continue;
            }

            ConfigEntry configEntry = field.getAnnotation(ConfigEntry.class);
            if(!configEntry.key().startsWith("permission.")) {
                continue;
            }

            field.setAccessible(true);
            map.put(configEntry.key(), (String) field.get(this));
        }
        return map;
    }

}
