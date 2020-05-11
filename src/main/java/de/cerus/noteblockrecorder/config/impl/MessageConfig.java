package de.cerus.noteblockrecorder.config.impl;

import de.cerus.noteblockrecorder.NoteblockRecorder;
import de.cerus.noteblockrecorder.config.Config;
import de.cerus.noteblockrecorder.config.annotations.ConfigEntry;
import de.cerus.noteblockrecorder.config.annotations.ConfigSettings;
import de.cerus.noteblockrecorder.config.transformer.ChatColorTransformer;
import de.cerus.noteblockrecorder.config.transformer.VariableTransformer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@ConfigSettings(header = "Noteblock Recorder Message Config")
public class MessageConfig extends Config {

    @ConfigEntry(key = "message.prefix", transformers = {ChatColorTransformer.class})
    public String prefix = "&5&lN&d&lBR &8\u25CF &7";

    @ConfigEntry(key = "message.please-wait", transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messagePleaseWait = "{PREFIX}&7&oPlease wait...";

    @ConfigEntry(key = "message.command.help", transformers = {ChatColorTransformer.class})
    public List<String> messageCommandHelp = Arrays.asList(
            "&5&lN&d&loteblock &5&lR&d&lecorder",
            "&e/nbr create <name>",
            "&e/nbr save",
            "&e/nbr browse",
            "&e/nbr play <name>",
            "&e/nbr delete <name>"
    );

    @ConfigEntry(key = "message.command.create-song.limit-reached", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandCreateSongLimitReached = "{PREFIX}&cYou have reached the max amount of songs.";

    @ConfigEntry(key = "message.command.create-song.already-recording", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandCreateSongAlreadyRecording = "{PREFIX}&cYou are already recording a song.";

    @ConfigEntry(key = "message.command.create-song.not-recording", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandCreateSongNotRecording = "{PREFIX}&cYou are not recording a song.";

    @ConfigEntry(key = "message.command.create-song.missing-name", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandCreateSongMissingName = "{PREFIX}&cThe song name is missing.";

    @ConfigEntry(key = "message.command.create-song.recording-started", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandCreateSongRecStarted = "{PREFIX}&aThe recording was started!";

    @ConfigEntry(key = "message.command.save-song.empty-song", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandSaveSongEmptySong = "{PREFIX}&cThe recorded song does not contain any sounds!";

    @ConfigEntry(key = "message.command.save-song.recording-ended", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandSaveSongRecEnded = "{PREFIX}&aThe recording was stopped!";

    @ConfigEntry(key = "message.command.play-song.missing-name", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandPlaySongMissingName = "{PREFIX}&cThe song name is missing.";

    @ConfigEntry(key = "message.command.play-song.unknown-song", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandPlaySongUnknownSong = "{PREFIX}&cFailed to find song.";

    @ConfigEntry(key = "message.command.play-song.playing-song", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandPlaySongPlayingSong = "{PREFIX}&aPlaying [SONG_NAME] by [SONG_AUTHOR]";

    @ConfigEntry(key = "message.command.play-song.already-playing", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandPlaySongAlreadyPlaying = "{PREFIX}&cYou are already playing a song.";

    @ConfigEntry(key = "message.command.stop-song.not-playing", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandStopSongNotPlaying = "{PREFIX}&cYou are not playing a song.";

    @ConfigEntry(key = "message.command.stop-song.song-stopped", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandStopSongStopped = "{PREFIX}&aThe playback was stopped!";

    @ConfigEntry(key = "message.command.delete-song.deleted", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandDeleteSongDeleted = "{PREFIX}&aThe song was successfully deleted!";

    @ConfigEntry(key = "message.command.delete-song.song-doesnt-exist", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageCommandDeleteSongSongDoesntExist = "{PREFIX}&cThe song doesn't exist.";

    @ConfigEntry(key = "message.inventory.manage-song.deleted", saveUntransformed = true, transformers = {ChatColorTransformer.class, VariableTransformer.class})
    public String messageInventoryManageSongDeleted = "{PREFIX}&aThe song was successfully deleted!";

    public MessageConfig(NoteblockRecorder plugin) {
        super(new File(plugin.getDataFolder(), "messages.yml"));
    }

    @Override
    protected void fieldLoaded(String fieldName, Object value) {
        if (!fieldName.equals("prefix")) {
            return;
        }

        VariableTransformer.VARIABLES.put("PREFIX", (String) value);
    }
}
