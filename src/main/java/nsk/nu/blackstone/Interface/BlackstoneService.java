package nsk.nu.blackstone.Interface;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BlackstoneService <T, Long> {

    CompletableFuture<T> getAsync(Long id);
    CompletableFuture<List<T>> getAllAsync();

    CompletableFuture<Void> saveAsync(T entity);
    CompletableFuture<Void> deleteAsync(T entity);
    CompletableFuture<Void> deleteByIdAsync(Long id);

    CompletableFuture<Void> deleteAllAsync();

    T get(Long id);
    List<T> getAll();
    boolean save(T entity);
    boolean delete(T entity);
    boolean deleteById(Long id);
    boolean deleteAll();
    
}
