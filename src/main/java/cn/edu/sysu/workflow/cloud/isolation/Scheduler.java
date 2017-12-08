package cn.edu.sysu.workflow.cloud.isolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler {

    public static abstract class Task implements Runnable {
        private final int weight;
        private final DeferredResult<String> result;

        public Task(int weight, DeferredResult<String> result) {

            this.weight = weight;
            this.result = result;
        }

        public int getWeight() {
            return weight;
        }
    }

    private Map<Integer, LinkedBlockingQueue<Task>> taskDeques = new HashMap<>();

    public void submit(Task task) {
        taskDeques.putIfAbsent(task.getWeight(), new LinkedBlockingQueue<>());
        taskDeques.get(task.getWeight()).add(task);
        inputSignals.offer(new Object());
    }

    private BlockingQueue<Object> signals = new LinkedBlockingQueue<>();

    private BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

    private BlockingQueue<Object> inputSignals = new LinkedBlockingQueue<>();

    private ExecutorService executor = Executors.newFixedThreadPool(3);


    public void finish() {
        try {
            signals.put(new Object());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    public Scheduler() {

        for (int i = 0; i < 20; i++) {
            try {
                signals.put(new Object());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        executor.execute(() -> {
            while (true) {

                if (queue.isEmpty()) {

                    try {
                        inputSignals.take();
                        inputSignals.put(new Object());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    taskDeques.forEach((integer, tasks) -> {

                        logger.info(integer + " " + tasks.size());
                        for (int i = 0; i < integer; i++) {
                            if (!tasks.isEmpty()) {
                                queue.add(tasks.poll());
                                inputSignals.poll();
                            }
                        }
                    });
                }

                logger.info("------------------------------------------------------");
                Task t;
                try {
                    t = queue.take();
                    signals.take();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                executor.submit(t);
            }
        });

    }


}
