package de.cerus.noteblockrecorder.song;

import de.cerus.noteblockrecorder.fileformat.SongFileFormat;
import de.cerus.noteblockrecorder.fileformat.SongIndexesFileFormat;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SongIndexes {

    private final Map<String, UUID> songs;
    private final Map<String, File> songFiles;
    private final Map<String, String> songNames;

    public SongIndexes(Map<String, UUID> songs, Map<String, File> songFiles, Map<String, String> songNames) {
        this.songs = songs;
        this.songFiles = songFiles;
        this.songNames = songNames;
    }

    /**
     * This method will attempt to load the default index file.
     *
     * @param songFolder The song folder
     * @return The song index
     * @author Cerus
     * @since 1.0.0
     */
    public static SongIndexes readDefault(File songFolder) {
        File indexFile = new File(songFolder, "song-indexes.sidx");
        if(!indexFile.exists()) {
            indexFile.getParentFile().mkdirs();
            try {
                indexFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileOutputStream fos = new FileOutputStream(indexFile); DataOutputStream out = new DataOutputStream(fos)) {
                new SongIndexesFileFormat(new SongIndexes(new HashMap<>(), new HashMap<>(), new HashMap<>())).writeToStream(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileInputStream fis = new FileInputStream(indexFile); DataInputStream in = new DataInputStream(fis)) {
            return new SongIndexesFileFormat(in).getIndexes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method will generate a new index if a song was added / removed.
     *
     * @param songFolder The song folder
     * @author Cerus
     * @since 1.0.0
     */
    public void reindexIfNeeded(File songFolder) {
        int count = 0;
        for (File file : songFolder.listFiles()) {
            if(file.isDirectory()) {
                continue;
            }

            if(!file.getName().endsWith(".nbrsong")) {
                continue;
            }

            count++;
        }

        if(count == songs.size()) {
            return;
        }

        songs.clear();
        songNames.clear();
        songFiles.clear();

        for (File file : songFolder.listFiles()) {
            if(file.isDirectory()) {
                continue;
            }

            if(!file.getName().endsWith(".nbrsong")) {
                continue;
            }

            try (FileInputStream fis = new FileInputStream(file); DataInputStream in = new DataInputStream(fis)) {
                Song song = new SongFileFormat(in).getSong();
                songs.put(song.getName().toUpperCase(), song.getAuthor());
                songFiles.put(song.getName().toUpperCase(), file);
                songNames.put(song.getName().toUpperCase(), song.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File indexFile = new File(songFolder, "song-indexes.sidx");
        try (FileOutputStream fos = new FileOutputStream(indexFile); DataOutputStream out = new DataOutputStream(fos)) {
            new SongIndexesFileFormat(this).writeToStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the songs map
     * @return Song:Author map
     * @author Cerus
     * @since 1.0.0
     */
    public Map<String, UUID> getSongs() {
        return songs;
    }

    /**
     * Getter for the songFiles map
     * @return Song:File map
     * @author Cerus
     * @since 1.0.0
     */
    public Map<String, File> getSongFiles() {
        return songFiles;
    }

    /**
     * Getter for the songNames map
     * @return Song:Name map
     * @author Cerus
     * @since 1.0.0
     */
    public Map<String, String> getSongNames() {
        return songNames;
    }

}
