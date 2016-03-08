package jaws.module;

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
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import jaws.module.base.Handle;
import jaws.module.standard.DefaultHandler;
import jaws.net.util.Handler;

public class ModuleLoader {
	
	private static final String moduleFolderPath = "D:\\Users\\Roy\\projects\\jaws\\modules";
	
	private static List<Entry<String, Handler>> handlers;
	
	static {
		List<Entry<Integer, Entry<String, Optional<Handler>>>> unsortedHandlers = new ArrayList<>();
		
		// add default handler
		System.out.println("Loading default handler");
		{
			Class<?> clazz = DefaultHandler.class;
			Method method = Arrays.asList(clazz.getDeclaredMethods())
			                      .stream()
			                      .filter(m -> m.isAnnotationPresent(Handle.class))
			                      .findFirst()
			                      .get();
			for(String extension : method.getAnnotation(Handle.class).extensions()) {
				unsortedHandlers.add(new SimpleEntry<>(method.getAnnotation(Handle.class).priority(),
				                                       new SimpleEntry<>(extension,
				                                                         Handler.from(method))));
			}
		}
		
		{	
			File moduleFolder = new File(moduleFolderPath);
			if(!moduleFolder.isDirectory()) {
				throw new RuntimeException("Module folder must be a directory");
			}
			System.out.println("Searching through modules folder");
			File[] files = moduleFolder.listFiles();
			System.out.println("Found " + files.length + " potential modules");
			for(File file : files) {
				System.out.println("Found file: " + file.getName());
				Optional<JarFile> optionalJar = tryCatch(() -> { return new JarFile(file); });
				if(!optionalJar.isPresent()) {
					continue;
				}
				JarFile jar = optionalJar.get();
				System.out.println("Found jar: " + jar.getName());
				Enumeration<JarEntry> jarEntries = jar.entries();
				while(jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();
					if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
						continue;
					}
					System.out.println("Found class: " + jarEntry.getName());

					tryCatch(() -> {
						URL[] urls = new URL[] { new URL("jar:file:" + jar.getName() + "!/") };
						URLClassLoader cl = new URLClassLoader(urls);
						System.out.println("Loaded ClassLoader");
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
						if(clazz == null) {
							return;
						}
						System.out.println("Loaded class");
						List<Method> methods = Arrays.asList(clazz.getDeclaredMethods())
						                             .stream()
						                             .filter(m -> m.isAnnotationPresent(Handle.class))
						                             .collect(Collectors.toList());
						for(Method method : methods) {
							System.out.println("Found method: " + method.getName());
							for(String extension : method.getAnnotation(Handle.class).extensions()) {
								unsortedHandlers.add(new SimpleEntry<>(method.getAnnotation(Handle.class).priority(),
								                                     new SimpleEntry<>(extension,
								                                                       Handler.from(method))));
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

		System.out.println("Finished loading modules");
	}
	
	private static Optional<Handler> getHandler(String extension) {
		
		return handlers.stream()
		               .filter(e -> extension.matches(e.getKey()))
		               .limit(1)
		               .map(e -> e.getValue())
		               .findFirst();
	}

	public static Function<String, Optional<Handler>> getHandlerGetter() {
		
		return ModuleLoader::getHandler;
	}
}
