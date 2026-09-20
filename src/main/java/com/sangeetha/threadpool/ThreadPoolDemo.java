package com.sangeetha.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolDemo {

    private static final int POOL_SIZE = 3;
    private static final int TOTAL_JOBS = 6;
    private static final int DEVICES_PER_JOB = 3;

    public static void main(String[] args) {
        AtomicInteger threadNumber = new AtomicInteger(1);

        ExecutorService threadPool = Executors.newFixedThreadPool(
                POOL_SIZE,
                runnable -> new Thread(
                        runnable,
                        "device-worker-" + threadNumber.getAndIncrement()
                )
        );

        try {
            for (int jobNumber = 1; jobNumber <= TOTAL_JOBS; jobNumber++) {
                String jobId = "JOB-" + jobNumber;
                threadPool.submit(() -> processDevices(jobId));
            }
        } finally {
            shutdownGracefully(threadPool);
        }

        System.out.println("All device jobs completed");
    }

    private static void processDevices(String jobId) {
        String workerName = Thread.currentThread().getName();
        System.out.printf("%s started on %s%n", jobId, workerName);

        for (int device = 1; device <= DEVICES_PER_JOB; device++) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("%s stopped because %s was interrupted%n", jobId, workerName);
                return;
            }

            System.out.printf(
                    "%s processed device-%d on %s%n",
                    jobId,
                    device,
                    workerName
            );

            if (!pause(500)) {
                System.out.printf("%s stopped during processing%n", jobId);
                return;
            }
        }

        System.out.printf("%s completed on %s%n", jobId, workerName);
    }

    private static boolean pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static void shutdownGracefully(ExecutorService threadPool) {
        threadPool.shutdown();

        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Timeout reached; interrupting unfinished jobs");
                threadPool.shutdownNow();

                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Some jobs could not be stopped");
                }
            }
        } catch (InterruptedException exception) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
