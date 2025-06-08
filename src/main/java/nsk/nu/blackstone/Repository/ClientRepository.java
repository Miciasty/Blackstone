package nsk.nu.blackstone.Repository;

import nsk.nu.blackstone.Entity.Client;
import nsk.nu.blackstone.Interface.BlackstoneRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClientRepository implements BlackstoneRepository<Client, Long> {

    private final SessionFactory sessionFactory;

    public ClientRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CompletableFuture<Client> findByIdAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> findById(id));
    }
    public CompletableFuture<Client> findByClientAsync(UUID clientUuid) {
        return CompletableFuture.supplyAsync(() -> findByClient(clientUuid));
    }
    public CompletableFuture<List<Client>> findAllAsync() {
        return CompletableFuture.supplyAsync(this::findAll);
    }
    public CompletableFuture<Void> saveAsync(Client entity) {
        return CompletableFuture.runAsync(() -> save(entity));
    }
    public CompletableFuture<Void> deleteAsync(Client entity) {
        return CompletableFuture.runAsync(() -> delete(entity));
    }
    public CompletableFuture<Void> deleteByIdAsync(Long id) {
        return CompletableFuture.runAsync(() -> deleteById(id));
    }
    public CompletableFuture<Void> deleteAllAsync() {
        return CompletableFuture.runAsync(this::deleteAll);
    }

    /* ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- */

    public Client findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Client.class, id);
        }
    }

    public Client findByClient(UUID clientUuid) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM client WHERE uuid = :uuid", Client.class)
                    .setParameter("uuid", clientUuid)
                    .uniqueResult();
        }
    }

    public List<Client> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM client", Client.class).getResultList();
        }
    }

    public boolean save(Client entity) {
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

    public boolean delete(Client entity) {
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
            session.createQuery("DELETE FROM client WHERE id = :id")
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
            session.createQuery("DELETE FROM client").executeUpdate();
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

}
