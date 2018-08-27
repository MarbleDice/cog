package com.bromleyoil.cog.persist.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the order of properties when presenting.
 * 
 * @author Billy
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PropertyOrder {

	String[] value();
}
