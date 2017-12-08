package cn.edu.sysu.workflow.cloud.isolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Gary
 */
public class Scheduler {

    public static abstract class Task implements Runnable {
        private final int weight;
        private final DeferredResult<String> result;

        protected Task(int weight, DeferredResult<String> result) {

            this.weight = weight;
            this.result = result;
        }

        public int getWeight() {
            return weight;
        }
    }

    private Map<Integer, LinkedBlockingQueue<Task>> weightTaskQueueMap = new TreeMap<>(Comparator.reverseOrder());

    public void submit(Task task) {
        weightTaskQueueMap.putIfAbsent(task.getWeight(), new LinkedBlockingQueue<>());
        weightTaskQueueMap.get(task.getWeight()).add(task);
        requestChannel.offer(new Object());
    }

    private BlockingQueue<Object> executeChannel = new LinkedBlockingQueue<>();

    private Queue<Task> taskOrderQueue = new ArrayDeque<>();

    private BlockingQueue<Object> requestChannel = new LinkedBlockingQueue<>();

    private final static int EXECUTE_CAPACITY = 20;

    private ExecutorService executor = Executors.newFixedThreadPool(EXECUTE_CAPACITY + 1);


    public void finish() {
        try {
            executeChannel.put(new Object());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public Scheduler() {

        for (int i = 0; i < EXECUTE_CAPACITY; i++) {
            try {
                executeChannel.put(new Object());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("Scheduler has started. Init capacity is {}", EXECUTE_CAPACITY);
        executor.execute(() -> {
            while (true) {

                if (taskOrderQueue.isEmpty()) {

                    try {
                        requestChannel.take();
                        requestChannel.put(new Object());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    weightTaskQueueMap.forEach((integer, tasks) -> {
                        for (int i = 0; i < integer; i++) {
                            if (!tasks.isEmpty()) {
                                taskOrderQueue.add(tasks.poll());
                                requestChannel.poll();
                            }
                        }
                    });
                }

                Task t;
                try {
                    t = taskOrderQueue.poll();
                    executeChannel.take();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                assert (t != null);
                executor.execute(t);
            }
        });

    }


}
