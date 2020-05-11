package de.cerus.noteblockrecorder.config.transformer;

import java.util.HashMap;
import java.util.Map;

public class VariableTransformer implements Transformer {

    public static final Map<String, String> VARIABLES = new HashMap<>();

    @Override
    public Object transform(String key, Object value) {
        if(value instanceof String) {
            String str = (String) value;
            for (Map.Entry<String, String> entry : VARIABLES.entrySet()) {
                str = str.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return str;
        }
        return value;
    }

    @Override
    public Object reverseTransform(String key, Object value) {
        return value;
    }

}
