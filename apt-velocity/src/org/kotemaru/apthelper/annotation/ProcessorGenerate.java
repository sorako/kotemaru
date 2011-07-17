package org.kotemaru.apthelper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ProcessorGenerate {
	String template();
	String pkg() default ".";
	String suffix() ;
}