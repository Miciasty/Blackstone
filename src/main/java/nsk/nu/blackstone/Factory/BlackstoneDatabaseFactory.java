package nsk.nu.blackstone.Factory;

import nsk.nu.blackstone.Interface.BlackstoneFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class BlackstoneDatabaseFactory implements BlackstoneFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r, "BlackstoneFactory.database-worker-" + threadNumber.getAndIncrement());
    }

    private static ExecutorService blackstoneExecutor;

    public static void create(ExecutorService e) {
        blackstoneExecutor = e;
    }
    public static void shutdown() {
        blackstoneExecutor.shutdown();
    }

    public static ExecutorService getExecutor() {
        return blackstoneExecutor;
    }

}