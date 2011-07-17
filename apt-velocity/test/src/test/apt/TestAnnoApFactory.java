package test.apt;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;

public class TestAnnoApFactory implements AnnotationProcessorFactory {
	private static final String TYPE_NAME = test.master.TestAnno.class.getName();
	private static final List OPTIONS = new ArrayList(0);
	private static final List TYPES = new ArrayList(1);
	static {
		TYPES.add(TYPE_NAME);
	}
	public Collection supportedOptions() {
		return OPTIONS;
	}

	public Collection<String> supportedAnnotationTypes() {
		return TYPES;
	}

	public AnnotationProcessor getProcessorFor(Set atds, AnnotationProcessorEnvironment env) {
		if (atds.contains(env.getTypeDeclaration(TYPE_NAME))) {
			return new TestAnnoAp(env);
		}
		return AnnotationProcessors.NO_OP;
	}

}
