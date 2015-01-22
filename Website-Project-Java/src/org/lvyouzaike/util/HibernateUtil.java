package org.lvyouzaike.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Log exception!
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory()
            throws HibernateException {
        return sessionFactory;
    }
}

/*public class HibernateUtil {

	private static SessionFactory sessionFactory = buildSessionFactory();
	private static ServiceRegistry serviceRegistry;

	private static SessionFactory buildSessionFactory()
			throws HibernateException {
		Configuration configuration = new Configuration();
		configuration.configure();
		serviceRegistry = (ServiceRegistry) new ServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	 * @SuppressWarnings("deprecation") private static SessionFactory
	 * buildSessionFactory() { try { // Create the SessionFactory from
	 * hibernate.cfg.xml return new
	 * Configuration().configure().buildSessionFactory(); } catch (Throwable ex)
	 * { // Make sure you log the exception, as it might be swallowed
	 * System.err.println("Initial SessionFactory creation failed." + ex); throw
	 * new ExceptionInInitializerError(ex); } }
	 

}
*/
