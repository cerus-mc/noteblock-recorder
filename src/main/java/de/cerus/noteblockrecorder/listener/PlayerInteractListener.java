package de.cerus.noteblockrecorder.listener;

import de.cerus.noteblockrecorder.song.RecorderController;
import de.cerus.noteblockrecorder.song.SongRecorder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private RecorderController recorderController;

    public PlayerInteractListener(RecorderController recorderController) {
        this.recorderController = recorderController;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("BLOCK")) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if(!(clickedBlock.getBlockData() instanceof NoteBlock)) {
            return;
        }

        Player player = event.getPlayer();
        if (!recorderController.isRecording(player.getUniqueId())) {
            return;
        }

        SongRecorder recorder = recorderController.getRecorder(player.getUniqueId());
        if(recorder.recordArea()) {
            return;
        }

        NoteBlock noteBlock = (NoteBlock) clickedBlock.getBlockData();
        recorder.addSound(noteBlock.getInstrument(), noteBlock.getNote());

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aSound added"));
    }
}
