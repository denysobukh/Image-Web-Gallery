package com.gallery.application;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * BackgroundExecutor class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 19:00 [Friday]
 */
@Component
public final class BackgroundExecutor {
    @Autowired
    private Logger logger;

    /**
     * number of CPUs to be used as maximum number of threads
     */
    private static final int CPUS_NUMBER = Runtime.getRuntime().availableProcessors();
    private static final int QUEUE_SIZE = 1000;
    private ExecutorService executor;

    @PostConstruct
    void postConstruct() {
        logger.info(CPUS_NUMBER + " cpu(s) found");
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        executor = new ThreadPoolExecutor(2, CPUS_NUMBER * 2, 1, TimeUnit.MINUTES, workQueue);
    }

    @PreDestroy
    private void preDestroy() {
        try {
            if (executor.awaitTermination(2, TimeUnit.SECONDS)) {
                System.out.println("Executors tasks completed");
            } else {
                System.out.println("Forcing shutdown of executor...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warn("Could not wait until executor termination", e);
        }
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(callable);
    }

    public void submit(Runnable runnable) {
        executor.submit(runnable);
    }

}
