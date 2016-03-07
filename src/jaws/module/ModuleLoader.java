package jaws.module;

import static trycrash.Try.tryCatch;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
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
	
	private static final String moduleFolderPath = "C:\\Users\\geroy\\projects\\jaws\\modules";
	
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

					URL[] urls;
					try {
						urls = new URL[] {(file.getParentFile().toURI().toURL())};
					} catch (MalformedURLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					URLClassLoader cl = new URLClassLoader(urls);
					Class<?> clazz;
					try {
                        
                        // Usually throws a NoClassDefFoundError
                        clazz = cl.loadClass(file.getName().substring(0, file.getName().length() - ".class".length()));
					}catch(NoClassDefFoundError e) {
                        
                        // Get Fully-Qualified-Classname from Error message
                        String fqName = e.getMessage().substring(e.getMessage().lastIndexOf(" ") + 1, e.getMessage().length() - 1).replace("/", ".");
                        clazz = Class.forName(fqName);
					}
					List<Method> methods = Arrays.asList(jarEntry.getClass().getDeclaredMethods())
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
				}
			}
		}
		
		handlers = unsortedHandlers.stream()
		                           .filter(e -> e.getValue().getValue().isPresent())
		                           .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
		                           .map(e -> new SimpleEntry<>(e.getValue().getKey(), e.getValue().getValue().get()))
		                           .collect(Collectors.toList());
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
