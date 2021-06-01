package de.cerus.noteblockrecorder.fileformat;

import de.cerus.noteblockrecorder.song.Song;
import de.cerus.noteblockrecorder.song.Sound;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SongFileFormat extends FileFormat {

    private Song song = null;

    public SongFileFormat(Song song) {
        super(null);
        this.song = song;
    }

    public SongFileFormat(DataInputStream inputStream) {
        super(inputStream);
        read(inputStream);
    }

    private void read(DataInputStream inputStream) {
        try {
            inputStream.readInt(); // Version
            String songName = readString(inputStream);
            String authorUuidStr = readString(inputStream);
            readString(inputStream); // Author name

            int sounds = inputStream.readInt();
            Set<Sound> soundsSet = new HashSet<>();

            for (int i = 0; i < sounds; i++) {
                soundsSet.add(new Sound(
                        Instrument.valueOf(readString(inputStream)),
                        new Note(
                                inputStream.readInt(),
                                Note.Tone.valueOf(readString(inputStream)),
                                inputStream.readByte() == ((byte) 1)
                        ),
                        inputStream.readInt()
                ));
            }

            song = new Song(songName, UUID.fromString(authorUuidStr), soundsSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToStream(DataOutputStream outputStream) {
        if (song == null) {
            throw new IllegalStateException();
        }

        try {
            outputStream.writeInt(1); // Format version
            writeString(outputStream, song.getName()); // Song name
            writeString(outputStream, song.getAuthor().toString()); // Author uuid
            writeString(outputStream, song.getAuthorName()); // Author name

            outputStream.writeInt(song.getSounds().size());
            for (Sound sound : song.getSounds()) {
                writeString(outputStream, sound.getInstrument().name()); // Instrument name
                outputStream.writeInt(sound.getNote().getOctave()); // Note octave
                writeString(outputStream, sound.getNote().getTone().name()); // Tone name
                outputStream.writeByte(((byte) (sound.getNote().isSharped() ? 1 : 0))); // Is note sharpened?
                outputStream.writeInt(sound.getDelay()); // Sound delay
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readString(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append((char) dis.readByte());
        }
        return builder.toString();
    }

    private void writeString(DataOutputStream dos, String str) throws IOException {
        dos.writeInt(str.length());
        for (char c : str.toCharArray()) {
            dos.writeByte((byte) c);
        }
    }

    @Override
    public String getExtension() {
        return "nbrsong";
    }

    public Song getSong() {
        return song;
    }

}
