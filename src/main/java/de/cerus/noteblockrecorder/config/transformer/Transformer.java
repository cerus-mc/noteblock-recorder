package de.cerus.noteblockrecorder.config.transformer;

public interface Transformer {
    Object transform(String key, Object value);

    default Object reverseTransform(String key, Object value) {
        return value;
    }
}
