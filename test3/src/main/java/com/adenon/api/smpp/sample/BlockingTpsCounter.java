package com.adenon.api.smpp.sample;


public class BlockingTpsCounter {

    private int  maxTps;
    private int  counter;
    private long startTime;

    public BlockingTpsCounter(final int maxTps) {
        this.setMaxTps(maxTps);
        this.startTime = System.currentTimeMillis();
    }

    public synchronized int increase() {
        this.counter++;
        if (this.counter > this.getMaxTps()) {
            final long delta = (System.currentTimeMillis() - this.startTime);
            if (delta < 1000) {
                try {
                    Thread.sleep(1000 - delta);
                } catch (final Exception e) {
                }
            }
            this.startTime = System.currentTimeMillis();
            this.counter = 1;
        }
        return this.counter;
    }


    public int getMaxTps() {
        return this.maxTps;
    }

    public void setMaxTps(final int maxTps) {
        this.maxTps = maxTps;
    }

    public static void main(final String[] args) {
        final BlockingTpsCounter counter = new BlockingTpsCounter(100);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            new Thread() {

                private final int myId = index;

                @Override
                public void run() {
                    while (true) {
                        final int lastCount = counter.increase();
                        System.out.println("Id: " + this.myId + " Time: " + System.currentTimeMillis() + " Count : " + lastCount);
                    }
                }
            }.start();
        }
    }

}
