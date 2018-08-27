package com.bromleyoil.cog.persist.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the property name to use when presenting a property value. Must annotate the property get method.
 * 
 * @author Billy
 *
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ReferencedBy {

	String value();
}
