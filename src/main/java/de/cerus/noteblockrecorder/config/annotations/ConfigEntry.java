package de.cerus.noteblockrecorder.config.annotations;

import de.cerus.noteblockrecorder.config.transformer.Transformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {

    String key();

    boolean saveUntransformed() default false;

    Class<? extends Transformer>[] transformers() default {};

}
