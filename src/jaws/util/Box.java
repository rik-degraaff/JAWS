package jaws.util;

import java.util.Optional;

public class Box<T> {

	private T content;

	public Box(T content) {

		this.content = content;
	}

	public Box() {

		this(null);
	}

	public void box(T content) {

		this.content = content;
	}

	public Optional<T> unbox() {

		return Optional.ofNullable(content);
	}
}
