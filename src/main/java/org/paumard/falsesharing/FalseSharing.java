package org.paumard.falsesharing;

public class FalseSharing {

    public static final long INTERACTIONS = 50_000_000L;
    public static int NUM_THREADS_MAX = 4;
    private static VolatileLongPadded[] paddedLongs;
    private static VolatileLongUnPadded[] unPaddedLongs;

    static {
        paddedLongs = new VolatileLongPadded[NUM_THREADS_MAX];
        for (int i = 0; i < paddedLongs.length; i++) {
            paddedLongs[i] = new VolatileLongPadded();
        }

        unPaddedLongs = new VolatileLongUnPadded[NUM_THREADS_MAX];
        for (int i = 0; i < unPaddedLongs.length; i++) {
            unPaddedLongs[i] = new VolatileLongUnPadded();
        }
    }

    public static void main(String[] args) throws Exception {
        runBanchMark();
    }

    private static void runBanchMark() throws InterruptedException {

        long begin, end;

        for (int n = 0; n < NUM_THREADS_MAX; n++) {
            Thread[] threads = new Thread[n];

            for (int j = 0; j < threads.length; j++) {
                threads[j] = new Thread(createPaddedRunnable(j));
            }

            begin = System.currentTimeMillis();
            for (Thread t: threads) t.start();
            for (Thread t: threads) t.join();
            end = System.currentTimeMillis();
            System.out.printf("Padded # thread %d - T = %dms\n", n, end - begin);

            for (int j = 0; j < threads.length; j++) {
                threads[j] = new Thread(createUnPaddedRunnable(j));
            }

            begin = System.currentTimeMillis();
            for (Thread t: threads) t.start();
            for (Thread t: threads) t.join();
            end = System.currentTimeMillis();
            System.out.printf("UnPadded # thread %d - T = %dms\n", n, end - begin);
        }
    }

    private static Runnable createUnPaddedRunnable(final int j) {
        return () -> {
            long i = INTERACTIONS + 1;
            while (0 != --i) {
                unPaddedLongs[j].value = i;
            }
        };
    }

    private static Runnable createPaddedRunnable(final int j) {
        return () -> {
          long i = INTERACTIONS + 1;
          while (0 != --i) {
              paddedLongs[j].value = i;
          }
        };
    }

    private static class VolatileLongPadded {
        public long q1, q2, q3, q4, q5, q6;
        public volatile long value = 0L;
        public long q11, q12, q13, q14, q15, q16;
    }

    private static class VolatileLongUnPadded {
        public volatile long value = 0L;
    }
}
