package com.bromleyoil.cog.persist.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the property name to use for representing a persistable class.
 * 
 * @author Billy
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PersistedBy {

	String value();
}
