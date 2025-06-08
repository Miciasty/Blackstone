package nsk.nu.blackstone.Modules.Lifesteal;

import nsk.nu.blackstone.Entity.Scenario;
import nsk.nu.blackstone.Interface.BlackstoneRepository;
import nsk.nu.blackstone.Modules.Lifesteal.Entity.Lifesteal;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LifestealRepository implements BlackstoneRepository<Lifesteal, Long> {

    private final SessionFactory sessionFactory;

    public LifestealRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CompletableFuture<Lifesteal> findByIdAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> findById(id));
    }
    public CompletableFuture<List<Lifesteal>> findAllByScenarioAsync(String scenarioName) {
        return CompletableFuture.supplyAsync(() -> findAllByScenario(scenarioName));
    }
    public CompletableFuture<Lifesteal> findByClientAndScenarioAsync(UUID clientUuid, String scenarioName) {
        return CompletableFuture.supplyAsync(() -> findByClientAndScenario(clientUuid, scenarioName));
    }
    public CompletableFuture<List<Lifesteal>> findAllAsync() {
        return CompletableFuture.supplyAsync(this::findAll);
    }
    public CompletableFuture<Void> saveAsync(Lifesteal entity) {
        return CompletableFuture.runAsync(() -> save(entity));
    }
    public CompletableFuture<Void> deleteAsync(Lifesteal entity) {
        return CompletableFuture.runAsync(() -> delete(entity));
    }
    public CompletableFuture<Void> deleteByIdAsync(Long id) {
        return CompletableFuture.runAsync(() -> deleteById(id));
    }
    public CompletableFuture<Void> deleteAllAsync() {
        return CompletableFuture.runAsync(this::deleteAll);
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    public Lifesteal findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Lifesteal.class, id);
        }
    }

    public List<Lifesteal> findAllByScenario(String scenarioName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM lifesteal_data WHERE scenario.name = :sname", Lifesteal.class)
                    .setParameter("sname", scenarioName)
                    .getResultList();
        }
    }

    public Lifesteal findByClientAndScenario(UUID clientUuid, String scenarioName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM lifesteal_data WHERE client.uuid = :uuid AND scenario.name = :sname", Lifesteal.class)
                    .setParameter("uuid", clientUuid)
                    .setParameter("sname", scenarioName)
                    .uniqueResult();
        }
    }


    public List<Lifesteal> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM lifesteal_data", Lifesteal.class).getResultList();
        }
    }

    public boolean save(Lifesteal entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public boolean delete(Lifesteal entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM lifesteal_data WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public boolean deleteAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM lifesteal_data").executeUpdate();
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
}
