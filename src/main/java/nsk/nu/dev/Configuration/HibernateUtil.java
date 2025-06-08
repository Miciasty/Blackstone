package nsk.nu.dev.Configuration;

import nsk.nu.blackstone.Entity.Client;
import nsk.nu.blackstone.Entity.Scenario;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.Lifesteal;
import nsk.nu.dev.Exceptions.HibernateBuildSessionFailure;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static void initialize() {
        if (sessionFactory == null) {
            try {
                Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
                Logger.getLogger("org.hibernate.SQL").setLevel(Level.OFF);
                Logger.getLogger("org.hibernate.type.descriptor.sql.BasicBinder").setLevel(Level.OFF);
                Properties p = new Properties();
                p.setProperty(Environment.DRIVER, nsk.nu.dev.Configuration.Environment.getDriver());
                p.setProperty(Environment.URL, nsk.nu.dev.Configuration.Environment.getUrl());
                p.setProperty(Environment.USER, nsk.nu.dev.Configuration.Environment.getUser());
                p.setProperty(Environment.PASS, nsk.nu.dev.Configuration.Environment.getPassword());
                p.setProperty(Environment.DIALECT, nsk.nu.dev.Configuration.Environment.getDialect());
                p.setProperty(Environment.HBM2DDL_AUTO, nsk.nu.dev.Configuration.Environment.getHbm2ddl());
                p.setProperty(Environment.SHOW_SQL, "false");
                p.setProperty(Environment.HBM2DDL_AUTO, "update");

                Configuration configuration = new Configuration()
                        .setProperties(p)

                        .addAnnotatedClass(Client.class)
                        .addAnnotatedClass(Scenario.class)

                        .addAnnotatedClass(Lifesteal.class);

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                throw new HibernateBuildSessionFailure("Unable to initialize the session factory object! ", e);
            }
        }
    }

    public static SessionFactory getSessionFactory() { return sessionFactory; }

    public static void close() {
        if (sessionFactory != null) sessionFactory.close();
    }

}
