package com.kitsunecode.mms.core.utils;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

public class ReflectionUtils {

    private static final Path EXTERNAL_LIB_PATH = Paths.get("adapters");

    private static Reflections reflections;

    private static Reflections getReflect() {
        if (reflections == null) {
            ConfigurationBuilder config = new ConfigurationBuilder()
                    .addUrls(ClasspathHelper.forJavaClassPath())
                    .addClassLoaders(getJarsClassLoader(), ReflectionUtils.class.getClassLoader());

            for(URL jarUrl : getExternalJarLibs()) {
                config.addUrls(ClasspathHelper.forManifest(jarUrl));
            }

            reflections = new Reflections(config);
        }
        return reflections;
    }

    private static URL asJarURL(File file) {
        try {
            return new URL("jar:" + file.toURI().toURL() + "!/");
        } catch (MalformedURLException ex) {
            ex.printStackTrace(); // Should not happen
            return null;
        }
    }

    private static URL[] getExternalJarLibs() {
        return Util.listFiles(EXTERNAL_LIB_PATH).stream()
                                                .filter(e -> e.getName().endsWith(".jar"))
                                                .map(ReflectionUtils::asJarURL)
                                                .filter(Objects::nonNull)
                                                .toArray(URL[]::new);
    }

    private static URLClassLoader getJarsClassLoader() {
        return URLClassLoader.newInstance(getExternalJarLibs());
    }

    public static Set<Class<?>> getAllClassesAnnotatedWith(Class<? extends Annotation> annotation) {
        return getReflect().getTypesAnnotatedWith(annotation);
    }

}
