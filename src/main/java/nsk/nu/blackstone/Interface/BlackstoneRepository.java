package nsk.nu.blackstone.Interface;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BlackstoneRepository <T, Long> {

    CompletableFuture<T> findByIdAsync(Long id);
    CompletableFuture<List<T>> findAllAsync();
    CompletableFuture<Void> saveAsync(T entity);
    CompletableFuture<Void> deleteAsync(T entity);
    CompletableFuture<Void> deleteByIdAsync(Long id);
    CompletableFuture<Void> deleteAllAsync();

    T findById(Long id);
    List<T> findAll();
    boolean save(T entity);
    boolean delete(T entity);
    boolean deleteById(Long id);
    boolean deleteAll();
}
