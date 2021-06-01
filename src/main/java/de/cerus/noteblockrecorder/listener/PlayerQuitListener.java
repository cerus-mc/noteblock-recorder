package de.cerus.noteblockrecorder.listener;

import de.cerus.noteblockrecorder.song.RecorderController;
import de.cerus.noteblockrecorder.song.SongController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private RecorderController recorderController;

    public PlayerQuitListener(RecorderController recorderController) {
        this.recorderController = recorderController;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!recorderController.isRecording(player.getUniqueId())) {
            return;
        }

        // Discord the recording
        recorderController.endRecording(player.getUniqueId());
    }

}
