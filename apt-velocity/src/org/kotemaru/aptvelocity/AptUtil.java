package org.kotemaru.aptvelocity;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.mozilla.javascript.NativeJavaPackage;

import com.sun.mirror.type.*;
import com.sun.mirror.declaration.*;

public class AptUtil {
	public static  boolean isPrivate(Declaration d)  {
		Collection<Modifier> mods = d.getModifiers();
		for (Modifier mod : mods)  {
			if (Modifier.PRIVATE.equals(mod))  {
				return true;
			} else if (Modifier.PROTECTED.equals(mod))  {
				return false;
			} else if (Modifier.PUBLIC.equals(mod))  {
				return false;
			}
		}
		return false;
	}

	public static  boolean isAbstract(Declaration d)  {
		Collection<Modifier> mods = d.getModifiers();
		for (Modifier mod : mods)  {
			if (Modifier.ABSTRACT.equals(mod))  {
				return true;
			}
		}
		return false;
	}

	public static String getModifiers(Declaration d, Modifier ignore) {
		Collection<Modifier> mods = d.getModifiers();
		if (mods.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(mods.size()*20);
		for (Modifier mod : mods)  {
			if (!mod.equals(ignore)) sbuf.append(mod);
			sbuf.append(' ');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	public static String getParams(ExecutableDeclaration d) {
		Collection<ParameterDeclaration> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (ParameterDeclaration param : params)  {
			sbuf.append(param.getType());
			sbuf.append(' ');
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	public static String getArguments(ExecutableDeclaration d) {
		Collection<ParameterDeclaration> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (ParameterDeclaration param : params)  {
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	public static String getThrows(ExecutableDeclaration d) {
		Collection<ReferenceType> params = d.getThrownTypes();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		sbuf.append("throws ");
		for (ReferenceType param : params)  {
			sbuf.append(param.toString());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	public static String getCaptalName( String name ) {
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}
	public static boolean hasAnnotation(TypeDeclaration d, Class cls ) throws Exception {
System.out.println("--->"+cls.getName());
return false;
//		return d.getAnnotation((Class<Annotation>)Class.forName(cname)) != null;
	}

}