package nsk.nu.blackstone.Repository;

import nsk.nu.blackstone.Entity.Scenario;
import nsk.nu.blackstone.Interface.BlackstoneRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScenarioRepository implements BlackstoneRepository<Scenario, Long> {

    private final SessionFactory sessionFactory;

    public ScenarioRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CompletableFuture<Scenario> findByIdAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> findById(id));
    }
    public CompletableFuture<Scenario> findByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> findByName(name));
    }
    public CompletableFuture<Scenario> findByNameIgnoreCaseAsync(String name) {
        return CompletableFuture.supplyAsync(() -> findByNameIgnoreCase(name));
    }
    public CompletableFuture<List<Scenario>> findAllAsync() {
        return CompletableFuture.supplyAsync(this::findAll);
    }
    public CompletableFuture<Void> saveAsync(Scenario entity) {
        return CompletableFuture.runAsync(() -> save(entity));
    }
    public CompletableFuture<Void> deleteAsync(Scenario entity) {
        return CompletableFuture.runAsync(() -> delete(entity));
    }
    public CompletableFuture<Void> deleteByIdAsync(Long id) {
        return CompletableFuture.runAsync(() -> deleteById(id));
    }
    public CompletableFuture<Void> deleteAllAsync() {
        return CompletableFuture.runAsync(this::deleteAll);
    }
    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    public Scenario findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Scenario.class, id);
        }
    }

    public Scenario findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM scenario WHERE name = :name", Scenario.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }
    public Scenario findByNameIgnoreCase(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM scenario WHERE UPPER(name) = UPPER(:name)", Scenario.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }

    public List<Scenario> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM scenario", Scenario.class).getResultList();
        }
    }

    public boolean save(Scenario entity) {
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

    public boolean delete(Scenario entity) {
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
            session.createQuery("DELETE FROM scenario WHERE id = :id")
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
            session.createQuery("DELETE FROM scenario").executeUpdate();
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

}
