package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            logger.error("Initial SessionFactory creation failed: {}", ex.getMessage());
            logger.error("SessionFactory creation failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void setSessionFactoryForTesting(SessionFactory testFactory) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        sessionFactory = testFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("SessionFactory closed");
        }
    }
}


