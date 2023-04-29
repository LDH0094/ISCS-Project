package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE, FIELD, TYPE_USE })
public @interface Column {

	public String name();
	public String sqlType();
	public boolean id() default false;
}
