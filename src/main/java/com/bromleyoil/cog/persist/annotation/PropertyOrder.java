package com.bromleyoil.cog.persist.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the order of properties in a {@link PersistedBy} entity.
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PropertyOrder {

	String[] value();
}
