package jaws.module;

import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import jaws.module.base.Handle;
import jaws.module.standard.DefaultHandler;
import jaws.net.util.Handler;

public class ModuleLoader {
	
	private static List<Entry<String, Handler>> handlers;
	
	static {
		List<Entry<Integer, Entry<String, Optional<Handler>>>> unsortedHandlers = new ArrayList<>();
		
		// add default handler
		{
			//Entry<Integer, Entry<String, Handler>> defaultHandler;
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
