package com.gallery.application;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * BackgroundTasksExecutor class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 19:00 [Friday]
 */
@Component
public class BackgroundTasksExecutor {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = new ThreadPoolExecutor(1, 2, 10, TimeUnit.MINUTES, queue);

    public void execute(Runnable task) {
        executor.execute(task);
    }
}
