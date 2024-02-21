package com.adenon.library.common.utils.tps;

import java.util.Arrays;

public class NonBlockingTpsCounter {

    public static final int HISTORY_COUNT = 120;
    private int             maxTps;
    private int             counter;
    private long            startTime;
    private final int[]     counterMemory;
    private int             memoryPointer;
    private long            lastMemoryUpdate;

    public NonBlockingTpsCounter(final int maxTps) {
        this.setMaxTps(maxTps);
        this.startTime = System.currentTimeMillis();
        this.counterMemory = new int[NonBlockingTpsCounter.HISTORY_COUNT];
        this.lastMemoryUpdate = System.currentTimeMillis();
        this.memoryPointer = 0;
    }

    public synchronized boolean increase() {
        return this.increaseBy(1);
    }

    public synchronized boolean increaseBy(final int tpsDelta) {
        final int currentCounter = this.counter;
        final int nextCounter = currentCounter + tpsDelta;
        final long delta = System.currentTimeMillis() - this.startTime;

        if (delta < 1000) {
            if (nextCounter > this.getMaxTps()) {
                // current period reached its TPS limit. So we should try
                // another connection.
                return false;
            } else {
                this.counter = nextCounter;
                return true;
            }
        } else { // next time period
            this.startTime = System.currentTimeMillis();
            this.counter = tpsDelta; // it is assumed that the TPS limit is
                                     // greater than concat message size anyway
            this.counterMemory[this.nextMemoryPointer()] = currentCounter;
            this.lastMemoryUpdate = System.currentTimeMillis();
            // System.out.println("Counter Memory: " +
            // Arrays.toString(this.counterMemory) + ", time = " + new Date());
            return true;
        }

        // int currentCounter = this.counter;
        // try {
        // long delta = System.currentTimeMillis() - this.startTime;
        // if (currentCounter >= this.getMaxTps()) {
        // if (delta < 1000) {
        // return false;
        // } else {
        // // this.counterMemory[this.nextMemoryPointer()] = currentCounter;
        // // System.out.println("Counter Memory: " +
        // Arrays.toString(this.counterMemory) + ", time = " + new Date());
        // this.startTime = System.currentTimeMillis();
        // this.counter = tpsDelta;
        // return true;
        // }
        // } else {
        // currentCounter += tpsDelta;
        // if (currentCounter > this.getMaxTps()) {
        // if (delta < 1000) {
        // return false;
        // } else {
        // // this.counterMemory[this.nextMemoryPointer()] = currentCounter;
        // // System.out.println("Counter Memory: " +
        // Arrays.toString(this.counterMemory) + ", time = " + new Date());
        // this.startTime = System.currentTimeMillis();
        // this.counter = tpsDelta;
        // return true;
        // }
        // } else {
        // this.counter = currentCounter;
        // return true;
        // }
        //
        // }
        // } finally {
        // if (System.currentTimeMillis() - this.lastMemoryTimestamp >= 1000) {
        // this.counterMemory[this.nextMemoryPointer()] = this.counter;
        // this.lastMemoryTimestamp = System.currentTimeMillis();
        // System.out.println("Counter Memory: " +
        // Arrays.toString(this.counterMemory) + ", time = " + new Date());
        // }
        // }
    }

    public int getAverageTps() {
        int total = 0;
        int nonzero = 0;

        int[] memory = null;
        synchronized (this) {
            memory = Arrays.copyOf(this.counterMemory, this.counterMemory.length);
        }

        for (final int c : memory) {
            if (c > 0) {
                ++nonzero;
                total += c;
            }
        }
        memory = null;

        if (nonzero > 0) {
            return total / nonzero;
        } else { // all memory filled by 0
            synchronized (this) {
                return this.counter;
            }
        }
    }

    public long getLastMemoryUpdate() {
        return this.lastMemoryUpdate;
    }

    private int nextMemoryPointer() {
        if (this.memoryPointer >= this.counterMemory.length) {
            this.memoryPointer = 0;
        }

        return this.memoryPointer++;
    }

    public void adjustTps(final int maxTps) {
        this.setMaxTps(maxTps);
    }

    public int getMaxTps() {
        return this.maxTps;
    }

    public int getCounter() {
        return this.counter;
    }

    @Override
    public String toString() {
        return "(C:" + this.counter + "/M:" + this.getMaxTps() + "/A:" + this.getAverageTps() + ")";
    }

    public static void main(final String[] args) {
        // final NonBlockingTpsCounter counter = new NonBlockingTpsCounter(10);
        // for (int i = 0; i < 30; i++) {
        // final int index = i;
        // new Thread() {
        //
        // private final int myId = index;
        //
        // @Override
        // public void run() {
        // SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss-SSS");
        // while (true) {
        // if (counter.increase()) {
        // System.out.println(dateFormat.format(new
        // Date(System.currentTimeMillis())) + "  - [SUCCESS] Id: " + this.myId
        // + " Time: "
        // + System.currentTimeMillis() + " counter : " + counter.getCounter());
        // } else {
        // // System.out.println(dateFormat.format(new
        // Date(System.currentTimeMillis()))
        // // + " - [FAIL] Id: "
        // // + this.myId
        // // + " Time: "
        // // + System.currentTimeMillis()
        // // + " counter : "
        // // + counter.getCounter());
        // try {
        // Thread.sleep(1);
        // } catch (Exception e) {
        // }
        // }
        // }
        // }
        // }.start();
        // }

        final NonBlockingTpsCounter c = new NonBlockingTpsCounter(10);
        System.out.println(c.nextMemoryPointer());
        System.out.println(c.nextMemoryPointer());
        System.out.println(c.nextMemoryPointer());
        System.out.println(c.nextMemoryPointer());
        System.out.println(c.nextMemoryPointer());
        System.out.println(c.nextMemoryPointer());
        System.out.println(c.nextMemoryPointer());
        System.out.println("" + (297932 / 271));
    }

    public void setMaxTps(final int maxTps) {
        this.maxTps = maxTps;
    }

}
