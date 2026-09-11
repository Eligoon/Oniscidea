package app.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleCalendarExecutor {

    private final ExecutorService executorService;

    public GoogleCalendarExecutor() {
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public void submit(Runnable task) {
        executorService.submit(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}