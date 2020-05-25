package com.bromleyoil.cog.persist.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates a referenced entity property, and specifies the property used to reference the entity. Must annotate the
 * property getter. Referenced entities are scoped to the parent {@link PersistedBy} entity, and fully loaded in a
 * second phase to resolve circular references.
 * 
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ReferencedBy {

	String value();
}
