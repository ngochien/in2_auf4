package de.hawhamburg.bank.services;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton context for the application that contains global resources.
 * 
 * Call {@link #initJMS(String, String)} and {@link #initJPA(String)} for
 * initializing the context.
 * 
 * Register own resources that should be closed at application shutdown by
 * {@link #registerClosable(Closable)}.
 * 
 * Make sure that {@link #close()} is called at application shutdown.
 */
public class ApplicationContext {
	private static final Logger LOG = LoggerFactory
			.getLogger(ApplicationContext.class);

	private static final ApplicationContext APPLICATION_CONTEXT = new ApplicationContext();

	public static ApplicationContext getApplicationContext() {
		return APPLICATION_CONTEXT;
	}

	// JMS
	private ConnectionFactory connectionFactory;
	private Connection connection;

	// JPA
	private EntityManagerFactory emf;
	private EntityManager em;

	private List<Closable> closables = new ArrayList<Closable>();

	private ApplicationContext() {
		super();
	}

	/**
	 * Initialize JMS resources.
	 * 
	 * After initialization, {@link #getJMSConnection()} can be called.
	 * 
	 * @param msgBrokerUrl
	 *            Message broker URL.
	 * @param clientId
	 *            Client id for the connection.
	 * 
	 * @throws JMSException
	 */
	public void initJMS(final String msgBrokerUrl, final String clientId)
			throws JMSException {
		LOG.debug("Initializing JMS for broker " + msgBrokerUrl
				+ " using client id " + clientId);
		connectionFactory = new ActiveMQConnectionFactory(msgBrokerUrl);
		connection = connectionFactory.createConnection();
		connection.setClientID(clientId);
	}

	/**
	 * Get the JMS connection of the application.
	 * 
	 * Make sure that {@link #initJMS(String, String)} is called prior to this
	 * method.
	 * 
	 * @return The JMS connection.
	 */
	public Connection getJMSConnection() {
		return this.connection;
	}

	/**
	 * Initialize JPA resources of the application.
	 * 
	 * After initialization, {@link #getEntityManager()} can be called.
	 * 
	 * @param persistenceUnitName
	 *            Name of the persistence unit in persistence.xml.
	 */
	public void initJPA(final String persistenceUnitName) {
		LOG.debug("Initializing JPA for persistence unit "
				+ persistenceUnitName);
		emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		em = emf.createEntityManager();
	}

	/**
	 * Get the JPA entity manager of the application.
	 * 
	 * Make sure that {@link #initJPA(String)} is called prior to this method.
	 * 
	 * @return The JPA entity manager.
	 */
	public EntityManager getEntityManager() {
		return this.em;
	}

	/**
	 * Register a resource that should be closed at application shutdown.
	 * 
	 * @param closable
	 *            The resource to be closed.
	 */
	public void registerClosable(final Closable closable) {
		if (!closables.contains(closable)) {
			closables.add(closable);
		}
	}

	/**
	 * Close all resources (JMS, JPA, registered).
	 */
	public void close() {
		LOG.debug("Closing resources");
		// Registered resources
		for (final Closable closable : closables) {
			try {
				closable.close();
			} catch (final Throwable e) {
				LOG.warn("while closing " + closable, e);
			}
		}
		closables.clear();
		// JMS
		if (connection != null) {
			try {
				connection.close();
			} catch (final Exception e) {
				LOG.warn("while closing connection", e);
			} finally {
				connection = null;
			}
		}
		this.connectionFactory = null;
		if (em != null) {
			try {
				em.close();
			} catch (final Exception e) {
				LOG.warn("while closing entity manager", e);
			} finally {
				em = null;
			}
		}
		// JPA
		if (emf != null) {
			try {
				emf.close();
			} catch (final Exception e) {
				LOG.warn("while closing entity manager factory", e);
			} finally {
				emf = null;
			}
		}
	}

}
