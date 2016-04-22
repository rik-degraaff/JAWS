package jaws.data.module;

import static trycrash.Try.tryCatch;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import jaws.business.defaultmodule.DefaultHandler;
import jaws.business.net.Handler;
import jaws.context.Context;
import jaws.module.http.RequestMethod;
import jaws.module.net.ContainsHandler;
import jaws.module.net.Handle;

public class ModuleLoader {

	private static List<Entry<Entry<String, RequestMethod>, Handler>> handlers;

	public static void init(String moduleFolderPath) {
		List<Entry<Integer, Entry<Entry<String, RequestMethod>, Optional<Handler>>>> unsortedHandlers = new ArrayList<>();

		// add default handler
		Context.logger.info("Loading default handler", "modules");
		{
			Class<?> clazz = DefaultHandler.class;
			Method method = Arrays.asList(clazz.getDeclaredMethods())
			                      .stream()
			                      .filter(m -> m.isAnnotationPresent(Handle.class))
			                      .findFirst()
			                      .get();
			for(String extension : method.getAnnotation(Handle.class).extensions()) {
				for(RequestMethod requestMethod : method.getAnnotation(Handle.class).methods()) {
					unsortedHandlers.add(new SimpleEntry<>(method.getAnnotation(Handle.class).priority(),
				                                           new SimpleEntry<>(new SimpleEntry<>(extension,
				                                                                               requestMethod),
				                                                             Handler.from(method))));
				}
			}
		}

		{
			File moduleFolder = new File(moduleFolderPath);
			if(!moduleFolder.isDirectory()) {
				throw new RuntimeException("Module folder must be a directory");
			}
			Context.logger.info("Searching through modules folder", "modules");
			File[] files = moduleFolder.listFiles();
			Context.logger.info("Found " + files.length + " potential modules", "modules");
			for(File file : files) {
				Context.logger.info("Found file: " + file.getName(), "modules");
				Optional<JarFile> optionalJar = tryCatch(() -> { return new JarFile(file); });
				if(!optionalJar.isPresent()) {
					continue;
				}
				JarFile jar = optionalJar.get();
				Context.logger.info("Found jar: " + jar.getName(), "modules");
				Enumeration<JarEntry> jarEntries = jar.entries();
				while(jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();
					if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
						continue;
					}
					Context.logger.info("Found class: " + jarEntry.getName(), "modules");

					tryCatch(() -> {
						URL[] urls = new URL[] { new URL("jar:file:" + jar.getName() + "!/") };
						URLClassLoader cl = new URLClassLoader(urls);
						Context.logger.info("Loaded ClassLoader", "modules");
						Class<?> clazz = null;
						try {

	                        // Usually throws a NoClassDefFoundError
	                        clazz = cl.loadClass(jarEntry.getName().substring(0, jarEntry.getName().length() - ".class".length()).replace("/", "."));
						}catch(NoClassDefFoundError | ClassNotFoundException e) {

	                        // Get Fully-Qualified-Classname from Error message
	                        String fqName = e.getMessage().substring(e.getMessage().lastIndexOf(" ") + 1, e.getMessage().length()).replace("/", ".");
	                        System.out.println(e.getMessage());
	                        try {
	                        	clazz = Class.forName(fqName);
	                        } catch(ClassNotFoundException e2) {
	                        	e2.printStackTrace();
	                        }
						}
						cl.close();
						if(clazz == null || !clazz.isAnnotationPresent(ContainsHandler.class)) {
							return;
						}
						Context.logger.info("Loaded class", "modules");
						List<Method> methods = Arrays.asList(clazz.getDeclaredMethods())
						                             .stream()
						                             .filter(m -> m.isAnnotationPresent(Handle.class))
						                             .collect(Collectors.toList());
						for(Method method : methods) {
							Context.logger.info("Found method: " + method.getName(), "modules");
							for(String extension : method.getAnnotation(Handle.class).extensions()) {
								for(RequestMethod requestMethod: method.getAnnotation(Handle.class).methods()) {
									unsortedHandlers.add(new SimpleEntry<>(method.getAnnotation(Handle.class).priority(),
								                                           new SimpleEntry<>(new SimpleEntry<>(extension,
								                                                                               requestMethod),
								                                                             Handler.from(method))));
								}
							}
						}
					});
				}
			}
		}

		handlers = unsortedHandlers.stream()
		                           .filter(e -> e.getValue().getValue().isPresent())
		                           .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
		                           .map(e -> new SimpleEntry<>(e.getValue().getKey(), e.getValue().getValue().get()))
		                           .collect(Collectors.toList());

		Context.logger.info("Finished loading modules", "modules");
	}

	private static Optional<Handler> getHandler(String extension, RequestMethod requestMethod) {

		return handlers.stream()
		               .filter(e -> extension.matches(e.getKey().getKey()))
		               .filter(e -> requestMethod.equals(e.getKey().getValue()))
		               .limit(1)
		               .map(e -> e.getValue())
		               .findFirst();
	}

	public static BiFunction<String, RequestMethod, Optional<Handler>> getHandlerGetter() {

		return ModuleLoader::getHandler;
	}
}
