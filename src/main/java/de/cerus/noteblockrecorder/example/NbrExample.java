package de.cerus.noteblockrecorder.example;

import com.google.common.collect.Sets;
import de.cerus.noteblockrecorder.api.NoteblockRecorderApi;
import de.cerus.noteblockrecorder.song.Song;
import de.cerus.noteblockrecorder.song.SongController;
import de.cerus.noteblockrecorder.song.SongIndexes;
import de.cerus.noteblockrecorder.song.Sound;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class NbrExample {

    private SongController songController = NoteblockRecorderApi.getSongController();
    private SongIndexes songIndexes = NoteblockRecorderApi.getSongIndexes();

    public void createSong() {
        String name = "My song";
        UUID author = UUID.randomUUID();

        HashSet<Sound> sounds = Sets.newHashSet(Arrays.asList(
                new Sound(Instrument.PIANO, new Note(1, Note.Tone.A, false), 5),
                new Sound(Instrument.PIANO, new Note(1, Note.Tone.C, false), 10),
                new Sound(Instrument.XYLOPHONE, new Note(1, Note.Tone.E, false), 15)
        ));

        Song song = new Song(name, author, sounds);
        songController.saveSongAsync(song, o -> {
            // Note: You cannot use most of the Bukkit api
            // inside of these async callbacks!
            System.out.println("Song was saved!");
        });
    }

}
