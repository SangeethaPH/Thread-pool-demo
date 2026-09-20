# Java Thread Pool Demo

A small Java 17 project showing how an `ExecutorService` manages and reuses worker threads.

## Scenario

The application submits six device-processing jobs to a fixed pool containing three threads. Each job processes three devices.

Because the pool has only three workers:

1. The first three jobs begin execution.
2. The remaining three jobs wait in the executor queue.
3. When a worker finishes, it takes another queued job.
4. The same worker thread is reused instead of creating a new thread for every job.

## Run

```bash
mvn compile
java -cp target/classes com.sangeetha.threadpool.ThreadPoolDemo
```

The exact output order can change between runs because thread scheduling is nondeterministic.

## Concepts demonstrated

- `ExecutorService`
- `Executors.newFixedThreadPool(3)`
- Submitting tasks with `submit()`
- Limiting concurrency with a fixed pool size
- Queuing tasks when all workers are busy
- Naming threads with a custom `ThreadFactory`
- Reusing worker threads
- Graceful shutdown with `shutdown()`
- Waiting with `awaitTermination()`
- Forced cancellation with `shutdownNow()`
- Cooperative interruption handling

## Execution model

```text
6 submitted jobs
       |
       v
Executor work queue
       |
       v
+----------+  +----------+  +----------+
| worker-1 |  | worker-2 |  | worker-3 |
+----------+  +----------+  +----------+
```

Only three jobs execute concurrently. The next queued job starts when one worker becomes available.

## Why use a thread pool?

Creating a new thread for every request is expensive and can exhaust CPU and memory under heavy load. A thread pool:

- controls concurrency;
- reuses existing threads;
- queues excess work;
- provides lifecycle and shutdown APIs.

## Important production note

`Executors.newFixedThreadPool()` uses an unbounded queue. It is convenient for learning, but a busy production service should normally use `ThreadPoolExecutor` with a bounded queue and an explicit rejection policy.

## Next lesson

Create a configurable `ThreadPoolExecutor` and examine:

- core pool size;
- maximum pool size;
- queue capacity;
- keep-alive time;
- rejection policies.
