package training.performance.threads;

import java.util.concurrent.CountDownLatch;

public class ThreadRunner {

    public static final int COUNT = 10;

    public static void main(final String[] args) {
        CountDownLatch countDownLatchLoc = new CountDownLatch(COUNT);
        for (int i = 0; i < COUNT; i++) {
            MyThread threadLoc = new MyThread(countDownLatchLoc);
            threadLoc.setName("mt-" + i);
            threadLoc.start();
        }

        try {
            countDownLatchLoc.await();
        } catch (InterruptedException eParam) {
            throw new RuntimeException(eParam);
        }
        System.out.println(MyThread.getCounter());

    }

}
