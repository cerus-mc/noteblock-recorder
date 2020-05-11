package de.cerus.noteblockrecorder.config.transformer;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class ChatColorTransformer implements Transformer {
    @Override
    public Object transform(String key, Object value) {
        if (value instanceof String) {
            return ChatColor.translateAlternateColorCodes('&', (String) value);
        }
        if (value instanceof String[]) {
            String[] arr = (String[]) value;
            for (int i = 0; i < arr.length; i++) {
                arr[i] = ChatColor.translateAlternateColorCodes('&', arr[i]);
            }
            return arr;
        }
        if (value instanceof List<?>) {
            return Arrays.asList((String[]) transform(key, ((List<?>) value).toArray(new String[0])));
        }
        if (value instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) value;
            int len = 0;
            while (iterable.iterator().hasNext()) {
                len++;
            }

            String[] arr = new String[len];

            int i = 0;
            for (Object o : iterable) {
                if (!(o instanceof String)) {
                    return value;
                }
                arr[i++] = (String) o;
            }

            return (Iterable<String>) Arrays.asList((String[]) transform(key, arr));
        }
        return value;
    }

    @Override
    public Object reverseTransform(String key, Object value) {
        if (value instanceof String) {
            String s = (String) value;
            return removeColor(s);
        }
        if (value instanceof String[]) {
            String[] arr = (String[]) value;
            for (int i = 0; i < arr.length; i++) {
                arr[i] = removeColor(arr[i]);
            }
            return arr;
        }
        if (value instanceof List<?>) {
            return Arrays.asList((String[]) reverseTransform(key, ((List<?>) value).toArray(new String[0])));
        }
        if (value instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) value;
            int len = 0;
            while (iterable.iterator().hasNext()) {
                len++;
            }

            String[] arr = new String[len];

            int i = 0;
            for (Object o : iterable) {
                if (!(o instanceof String)) {
                    return value;
                }
                arr[i++] = (String) o;
            }

            return (Iterable<String>) Arrays.asList((String[]) reverseTransform(key, arr));
        }
        return value;
    }

    private String removeColor(String s) {
        // Stolen from Spigot's code

        char[] b = s.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '§' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '&';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
