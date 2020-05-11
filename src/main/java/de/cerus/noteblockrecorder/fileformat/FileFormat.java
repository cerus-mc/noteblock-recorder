package de.cerus.noteblockrecorder.fileformat;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class FileFormat {

    public FileFormat(DataInputStream inputStream) {
    }

    public abstract void writeToStream(DataOutputStream outputStream);

    public abstract String getExtension();

}
