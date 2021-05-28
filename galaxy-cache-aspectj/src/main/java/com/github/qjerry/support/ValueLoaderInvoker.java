package com.github.qjerry.support;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
public interface ValueLoaderInvoker {

	Object invoke() throws ThrowableWrapperException;

	class ThrowableWrapperException extends RuntimeException {
		private final Throwable original;

		public ThrowableWrapperException(Throwable original) {
			super(original.getMessage(), original);
			this.original = original;
		}

		public Throwable getOriginal() {
			return this.original;
		}
	}
}
