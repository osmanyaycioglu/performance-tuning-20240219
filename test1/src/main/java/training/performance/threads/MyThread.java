package training.performance.threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class MyThread extends Thread {

    public static AtomicLong     counter = new AtomicLong();
    public static long counterLong = 0;
    private long localCounter = 0;
    private       CountDownLatch countDownLatch;

    public MyThread(final CountDownLatch countDownLatchParam) {
        countDownLatch = countDownLatchParam;
    }

    public static synchronized void inc(){
        counterLong++;
    }

    public static synchronized void add(long c){
        counterLong +=c;
    }

    public static long getCounter(){
        return counterLong;
    }

    @Override
    public void run() {
        try {
            long delta = System.currentTimeMillis();
            for (int i = 0; i < 100_000_000; i++) {
                // counter.incrementAndGet();
                localCounter++;
            }
            System.out.println("TH : " + Thread.currentThread()
                                               .getName() + " delta : " + (System.currentTimeMillis() - delta));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        add(localCounter);
        countDownLatch.countDown();
    }

}
