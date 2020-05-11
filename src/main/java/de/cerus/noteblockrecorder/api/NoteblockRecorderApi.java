package de.cerus.noteblockrecorder.api;

import de.cerus.noteblockrecorder.song.SongController;
import de.cerus.noteblockrecorder.song.SongIndexes;

import java.io.File;

public class NoteblockRecorderApi {

    private static boolean initialized = false;
    private static SongController songController;
    private static SongIndexes songIndexes;
    private static File songFolder;

    private NoteblockRecorderApi() {
        throw new UnsupportedOperationException();
    }

    private static void init(SongController songController, SongIndexes songIndexes, File songFolder) {
        if (initialized) {
            return;
        }

        NoteblockRecorderApi.songController = songController;
        NoteblockRecorderApi.songIndexes = songIndexes;
        NoteblockRecorderApi.songFolder = songFolder;
        initialized = true;
    }

    private static void destroy() {
        NoteblockRecorderApi.songController = null;
        NoteblockRecorderApi.songIndexes = null;
        NoteblockRecorderApi.songFolder = null;
        initialized = false;
    }

    /**
     * @author Cerus
     * @since 1.0.0
     * @return The current SongController instance
     */
    public static SongController getSongController() {
        return songController;
    }

    /**
     * @author Cerus
     * @since 1.0.0
     * @return The current SongIndexes instance
     */
    public static SongIndexes getSongIndexes() {
        return songIndexes;
    }

    /**
     * @author Cerus
     * @since 1.0.0
     * @return The current song folder
     */
    public static File getSongFolder() {
        return songFolder;
    }

}
