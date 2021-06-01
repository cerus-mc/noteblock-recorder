package de.cerus.noteblockrecorder.fileformat;

import de.cerus.noteblockrecorder.song.SongIndexes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SongIndexesFileFormat extends FileFormat {
    private SongIndexes indexes = null;

    public SongIndexesFileFormat(SongIndexes indexes) {
        super(null);
        this.indexes = indexes;
    }

    public SongIndexesFileFormat(DataInputStream inputStream) {
        super(inputStream);
        read(inputStream);
    }

    private void read(DataInputStream inputStream) {
        try {
            int songs = inputStream.readInt();
            Map<String, UUID> authorMap = new HashMap<>();
            Map<String, File> fileMap = new HashMap<>();
            Map<String, String> nameMap = new HashMap<>();

            for (int i = 0; i < songs; i++) {
                String name = readString(inputStream);
                authorMap.put(name.toUpperCase(), UUID.fromString(readString(inputStream)));
                fileMap.put(name.toUpperCase(), new File(readString(inputStream)));
                nameMap.put(name.toUpperCase(), name);
            }

            indexes = new SongIndexes(authorMap, fileMap, nameMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToStream(DataOutputStream outputStream) {
        try {
            outputStream.writeInt(indexes.getSongs().size());
            for (Map.Entry<String, UUID> entry : indexes.getSongs().entrySet()) {
                writeString(outputStream, indexes.getSongNames().get(entry.getKey()));
                writeString(outputStream, entry.getValue().toString());
                writeString(outputStream, indexes.getSongFiles().get(entry.getKey()).getAbsolutePath());
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
        return "sidx";
    }

    public SongIndexes getIndexes() {
        return indexes;
    }
}
