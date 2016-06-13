package jaws.util;

import java.util.Optional;

/**
 * A wrapper class for transporting references through the application.
 * 
 * @author Roy
 *
 * @param <T> the type of the reference.
 */
public class Box<T> {

	private T content;

	public Box(T content) {

		this.content = content;
	}

	public Box() {

		this(null);
	}

	/**
	 * Put something into the box. Overwrites any previous content of this box.
	 * 
	 * @param content the reference to store.
	 */
	public void box(T content) {

		this.content = content;
	}

	/**
	 * Returns an {@link java.util.Optional} containing the box's content or an empty {@link java.util.Optional}.
	 * 
	 * @return
	 */
	public Optional<T> unbox() {

		return Optional.ofNullable(content);
	}
}
