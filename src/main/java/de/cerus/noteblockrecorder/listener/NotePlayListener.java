package de.cerus.noteblockrecorder.listener;

import de.cerus.noteblockrecorder.song.RecorderController;
import de.cerus.noteblockrecorder.song.SongRecorder;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;

public class NotePlayListener implements Listener {

    private RecorderController recorderController;

    public NotePlayListener(RecorderController recorderController) {
        this.recorderController = recorderController;
    }

    @EventHandler
    public void onNotePlay(NotePlayEvent event) {
        Block block = event.getBlock();
        SongRecorder recorder = recorderController.getRecorder(block.getLocation());
        if(recorder == null) {
            return;
        }

        recorder.addSound(event.getInstrument(), event.getNote());
    }
}
