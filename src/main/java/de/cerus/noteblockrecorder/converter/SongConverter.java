package de.cerus.noteblockrecorder.converter;

import de.cerus.noteblockrecorder.song.Song;
import de.cerus.noteblockrecorder.song.Sound;

import java.io.*;

public class SongConverter {

    /*
        Note: Everything in this class is TODO.
     */

    private SongConverter() {
    }

    //TODO
    public static void convert(Song song, File outputFile) throws IOException {
   /*     if(!outputFile.exists()) {
            if(outputFile.getParentFile() != null) {
                outputFile.mkdirs();
            }
            outputFile.createNewFile();
        }

        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile));
        outputStream.writeShort((short) song.getLengthInTicks()); // Song length
        outputStream.writeShort((short) 1); // Highest noteblock layer
        outputStream.writeChars(song.getName()); // Song name
        outputStream.writeChars(song.getAuthorName()); // Song author
        outputStream.writeChars(song.getAuthorName()); // Original song author
        outputStream.writeChars("No description"); // Song description
        outputStream.writeShort((short) (20 * 100)); // Song tempo
        outputStream.writeByte((byte) 0); // Auto saving
        outputStream.writeByte((byte) 60); // Auto saving duration
        outputStream.writeByte((byte) 4); // Time signature
        outputStream.writeInt((song.getLengthInTicks()/20)/60); // Minutes spent
        outputStream.writeInt(0); // Left clicks
        outputStream.writeInt(0); // Right clicks
        outputStream.writeInt(0); // Blocks added
        outputStream.writeInt(0); // Blocks removed

        int currentTick = -1;
        int currentLayer = -1;

        for (Sound sound : song.getSounds()) {

        }*/
    }

    //TODO
    public static void convert(File nbsFile, File outputFile) throws IOException {
        //DataInputStream in = new DataInputStream(new FileInputStream(nbsFile));
    }

}
