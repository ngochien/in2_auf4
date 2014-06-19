package de.hawhamburg.bank.services;

/**
 * Interface to mark a resource that should be closed at application shutdown.
 * 
 * Register this resource via
 * {@link ApplicationContext#registerClosable(Closable)}.
 */
public interface Closable {

	/**
	 * Close this resource.
	 */
	void close();

}
