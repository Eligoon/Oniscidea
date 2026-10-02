package app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleCalendarExecutor {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    GoogleCalendarExecutor.class
            );

    private final ExecutorService executorService;

    public GoogleCalendarExecutor() {
        this.executorService =
                Executors.newFixedThreadPool(5);
    }

    public void submit(Runnable task) {

        executorService.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error(
                        "Error while executing Google Calendar task",
                        e
                );
            }
        });
    }

    public void shutdown() {

        logger.info(
                "Shutting down Google Calendar executor"
        );

        executorService.shutdown();
    }
}