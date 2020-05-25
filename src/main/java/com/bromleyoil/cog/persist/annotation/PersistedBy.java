package com.bromleyoil.cog.persist.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates a persistable entity, and specifies the property used to reference it.
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PersistedBy {

	String value();
}
