package de.cerus.noteblockrecorder.config;

import de.cerus.noteblockrecorder.config.annotations.ConfigEntry;
import de.cerus.noteblockrecorder.config.annotations.ConfigSettings;
import de.cerus.noteblockrecorder.config.transformer.Transformer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private final Map<String, Object> untransformedValues = new HashMap<>();

    private final File file;
    private final FileConfiguration configuration;

    public Config(File file) {
        this.file = file;
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    protected void fieldLoaded(String fieldName, Object value) {
    }

    public void init() throws IllegalAccessException, IOException {
        if(configuration.getKeys(true).isEmpty()) {
            save();
        }

        load();
    }

    public void load() throws IllegalAccessException {
        Class<? extends Config> clazz = getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigEntry.class)) {
                continue;
            }

            ConfigEntry configEntry = field.getAnnotation(ConfigEntry.class);

            String key = configEntry.key();
            if (key.equals("") || key.matches("\\s+")) {
                continue;
            }

            if(!configuration.contains(key)) {
                continue;
            }

            field.setAccessible(true);
            Object value = configuration.get(key);

            untransformedValues.put(field.getName(), value);

            if(configEntry.transformers().length >= 1) {
                for (Class<? extends Transformer> transformerClass : configEntry.transformers()) {
                    try {
                        Transformer transformer = transformerClass.newInstance();
                        value = transformer.transform(key, value);
                    } catch (InstantiationException ignored) {
                    }
                }
            }

            field.set(this, value);

            fieldLoaded(field.getName(), value);
        }
    }

    public void save() throws IllegalAccessException, IOException {
        Class<? extends Config> clazz = getClass();

        if (clazz.isAnnotationPresent(ConfigSettings.class)) {
            ConfigSettings configSettings = clazz.getAnnotation(ConfigSettings.class);

            String header = configSettings.header();
            if (!header.equals("") && !header.matches("\\s+")) {
                configuration.options().header(header);
            }
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigEntry.class)) {
                continue;
            }

            ConfigEntry configEntry = field.getAnnotation(ConfigEntry.class);

            String key = configEntry.key();
            if (key.equals("") || key.matches("\\s+")) {
                continue;
            }

            field.setAccessible(true);
            Object obj = field.get(this);
            if (obj == null) {
                continue;
            }

            if(!configEntry.saveUntransformed()) {
                if(configEntry.transformers().length >= 1) {
                    for (Class<? extends Transformer> transformerClass : configEntry.transformers()) {
                        try {
                            Transformer transformer = transformerClass.newInstance();
                            obj = transformer.reverseTransform(key, obj);
                        } catch (InstantiationException ignored) {
                        }
                    }
                }
            } else {
                obj = untransformedValues.getOrDefault(field.getName(), obj);
            }

            configuration.set(key, obj);

            fieldLoaded(field.getName(), obj);
        }

        configuration.save(file);
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

}
