package de.cerus.noteblockrecorder.song;

import de.cerus.noteblockrecorder.util.Cuboid;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SongRecorder {

    private String songName;
    private UUID author;
    private long startTime;
    private Set<Sound> sounds;
    private Cuboid cuboid;

    public SongRecorder(String songName, UUID author) {
        this(songName, author, null);
    }

    public SongRecorder(String songName, UUID author, Cuboid cuboid) {
        this.songName = songName;
        this.author = author;
        this.startTime = System.currentTimeMillis();
        this.sounds = new HashSet<>();
        this.cuboid = cuboid;
    }

    public void addSound(Instrument instrument, Note note) {
        addSound(new Sound(instrument, note, ((int) (System.currentTimeMillis() - startTime) / 50)));
    }

    public void addSound(Sound sound) {
        sounds.add(sound);
    }

    public boolean recordArea() {
        return cuboid != null;
    }

    public Song toSong() {
        return new Song(songName, author, sounds);
    }

    public UUID getAuthor() {
        return author;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }
}
