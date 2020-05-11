package de.cerus.noteblockrecorder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.cerus.noteblockrecorder.config.impl.MessageConfig;
import de.cerus.noteblockrecorder.config.impl.PluginConfig;
import de.cerus.noteblockrecorder.song.RecorderController;
import de.cerus.noteblockrecorder.song.Song;
import de.cerus.noteblockrecorder.song.SongController;
import de.cerus.noteblockrecorder.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("noteblockrecorder|nbrecorder|nbrec|nbr")
@CommandPermission("%permission.use-command")
public class NoteblockRecorderCommand extends BaseCommand {

    private PluginConfig pluginConfig;
    private MessageConfig messageConfig;
    private RecorderController recorderController;
    private SongController songController;

    public NoteblockRecorderCommand(PluginConfig pluginConfig, MessageConfig messageConfig, RecorderController recorderController, SongController songController) {
        this.pluginConfig = pluginConfig;
        this.messageConfig = messageConfig;
        this.recorderController = recorderController;
        this.songController = songController;
    }

    @Default
    @CatchUnknown
    @Subcommand("help")
    public void sendHelp(Player player) {
        messageConfig.messageCommandHelp.forEach(player::sendMessage);
    }

    @Subcommand("create")
    @CommandPermission("%permission.create-songs")
    @CommandCompletion("<name> @looking_at @looking_at")
    public void createSong(Player player, String[] args) {
        if (recorderController.isRecording(player.getUniqueId())) {
            player.sendMessage(messageConfig.messageCommandCreateSongAlreadyRecording);
            return;
        }

        if (pluginConfig.songsPerUser != -1 && pluginConfig.songsPerUser <= songController.getPlayerSongs(player)
                && !player.hasPermission(pluginConfig.permissionLimitBypass)) {
            player.sendMessage(messageConfig.messageCommandCreateSongLimitReached);
            return;
        }

        if (args.length == 0) {
            player.sendMessage(messageConfig.messageCommandCreateSongMissingName);
            return;
        }

        String songName = String.join(" ", args);
        Cuboid cuboid = null;

        if (args.length >= 3) {
            String lastArg1 = args[args.length - 1];
            String lastArg2 = args[args.length - 2];

            if (lastArg1.matches("-?[0-9.]+;-?[0-9.]+;-?[0-9.]+")
                    && lastArg2.matches("-?[0-9.]+;-?[0-9.]+;-?[0-9.]+")) {
                Location loc1 = parseLocation(lastArg1, player);
                Location loc2 = parseLocation(lastArg2, player);

                if (loc1 != null && loc2 != null) {
                    songName = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 2));
                    cuboid = new Cuboid(loc1, loc2);
                }
            }
        }

        recorderController.startRecording(player.getUniqueId(), songName, cuboid);
        player.sendMessage(messageConfig.messageCommandCreateSongRecStarted);
    }

    @Subcommand("save")
    @CommandPermission("%permission.create-songs")
    public void saveSong(Player player) {
        if (!recorderController.isRecording(player.getUniqueId())) {
            player.sendMessage(messageConfig.messageCommandCreateSongNotRecording);
            return;
        }

        Song song = recorderController.endRecording(player.getUniqueId());
        if (song.getSounds().isEmpty()) {
            player.sendMessage(messageConfig.messageCommandSaveSongEmptySong);
            return;
        }

        player.sendMessage(messageConfig.messagePleaseWait);
        songController.saveSongAsync(song, o ->
                player.sendMessage(messageConfig.messageCommandSaveSongRecEnded));
    }

    @Subcommand("play")
    @CommandCompletion("@songs")
    @CommandPermission("%permission.play-songs")
    public void playSong(Player player, String[] args) {
        if (songController.isSongPlaying(player)) {
            player.sendMessage(messageConfig.messageCommandPlaySongAlreadyPlaying);
            return;
        }

        if (args.length == 0) {
            player.sendMessage(messageConfig.messageCommandPlaySongMissingName);
            return;
        }

        String songName = String.join(" ", args);
        if (!songController.doesSongExist(songName)) {
            player.sendMessage(messageConfig.messageCommandPlaySongUnknownSong);
            return;
        }

        player.sendMessage(messageConfig.messagePleaseWait);
        songController.loadSongAsync(songName, song -> {
            songController.playSong(player, song);

            player.sendMessage(messageConfig.messageCommandPlaySongPlayingSong
                    .replace("[SONG_NAME]", song.getName())
                    .replace("[SONG_AUTHOR]", song.getAuthorName()));
        });
    }

    @Subcommand("stop")
    @CommandPermission("%permission.play-songs")
    public void stopSong(Player player) {
        if (!songController.isSongPlaying(player)) {
            player.sendMessage(messageConfig.messageCommandStopSongNotPlaying);
            return;
        }

        songController.stopSong(player);
        player.sendMessage(messageConfig.messageCommandStopSongStopped);
    }

    @Subcommand("browse")
    @CommandPermission("%permission.browse-songs")
    public void browseSongs(Player player) {
        player.openInventory(songController.newSongsInventory(player).getInventory());
    }

    @Subcommand("delete")
    @CommandCompletion("@songs")
    @CommandPermission("%permission.manage-songs")
    public void deleteSong(Player player, String[] args) {
        String songName = String.join(" ", args);
        if (!songController.doesSongExist(songName)) {
            player.sendMessage(messageConfig.messageCommandDeleteSongSongDoesntExist);
            return;
        }

        player.sendMessage(messageConfig.messagePleaseWait);
        songController.loadSongAsync(songName, song -> songController.deleteSongAsync(song, o ->
                player.sendMessage(messageConfig.messageCommandDeleteSongDeleted)));
    }

    private Location parseLocation(String str, Player player) {
        String[] split = str.split(";");
        try {
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            return new Location(player.getWorld(), x, y, z);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

}
