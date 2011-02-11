package es.elv.osgi.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Locates a SCR service on Feature activate and puts
 * it into the field this annotation belongs to.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autolocate {

	/**
	 * The Service to locate; defaults to the field type (required for method()).
	 */
	public Class<?> type() default Autolocate.class;

	/**
	 * A zero-parameter method to call on the located Service (via type()); the
	 * result of which will be assigned to the annotated field.
	 */
	public String method() default "";
}
