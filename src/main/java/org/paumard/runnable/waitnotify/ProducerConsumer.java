package org.paumard.runnable.waitnotify;

public class ProducerConsumer {

    private static Object lock = new Object();
    private static int[] buffer;
    private static int count;

    private static boolean isFull(int[] buffer) {
        return count == buffer.length;
    }

    private static boolean isEmpty(int[] buffer) {
        return count == 0;
    }

    public static void main(String... args) throws InterruptedException {

        buffer = new int[10];
        count = 0;

        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Runnable produceTask = () -> {
            for (int i = 0; i < 50; i++) {
                producer.producer();
                System.out.println("Done producing " + Thread.currentThread().getName());
            }
        };

        Runnable consumeTask = () -> {
            for (int i = 0; i < 50; i++) {
                consumer.consumer();
                System.out.println("Done consuming " + Thread.currentThread().getName());
            }
        };


        Thread consumerThread = new Thread(consumeTask);
        Thread producerThread = new Thread(produceTask);

        consumerThread.start();
        producerThread.start();

        consumerThread.join();
        producerThread.join();

        System.out.println("Data in the buffer: " + count);
    }

    static class Producer {

        void producer() {
            synchronized (lock) {
                if (isFull(buffer)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                buffer[count++] = 1;
                lock.notifyAll();
            }
        }
    }

    static class Consumer {

        void consumer() {
            synchronized (lock) {
                if (isEmpty(buffer)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                buffer[--count] = 0;
                lock.notifyAll();
            }
        }
    }
}
