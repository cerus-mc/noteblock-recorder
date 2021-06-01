package de.cerus.noteblockrecorder.song;

import de.cerus.noteblockrecorder.util.Cuboid;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecorderController {

    private final List<SongRecorder> recorders = new ArrayList<>();

    public void startRecording(UUID author, String songName, Cuboid cuboid) {
        recorders.add(new SongRecorder(songName, author, cuboid));
    }

    public Song endRecording(UUID author) {
        SongRecorder songRecorder = getRecorder(author);
        if(songRecorder != null) {
            recorders.remove(songRecorder);
            return songRecorder.toSong();
        }
        return null;
    }

    public boolean isRecording(UUID uuid) {
        return getRecorder(uuid) != null;
    }

    public SongRecorder getRecorder(UUID uuid) {
        return recorders.stream()
                .filter(songRecorder -> songRecorder.getAuthor().equals(uuid))
                .findAny().orElse(null);
    }

    public SongRecorder getRecorder(Location location) {
        return recorders.stream()
                .filter(SongRecorder::recordArea)
                .filter(songRecorder -> songRecorder.getCuboid().containsLocation(location))
                .findAny().orElse(null);
    }

}
