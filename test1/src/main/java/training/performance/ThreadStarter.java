package training.performance;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadStarter {


    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            MyThread threadLoc = new MyThread();
            threadLoc.setName("MyThread-" + i);
            threadLoc.start();
        }
    }


    public static class MyThread extends Thread {
        private        long             counter;
        private static Object           object = new Object();
        private static Map<String, Car> map    = new ConcurrentHashMap<>(1_000_000,
                                                                         0.9f,
                                                                         1_000);

        @Override
        public void run() {
            try {
                Thread.sleep(10_000);
            } catch (Exception exp) {
            }
            while (true) {
                try {
                    counter++;
                    testPerformance("osman" + counter, 1000);
                    if (counter % 100 == 0) {
                        map.put("bwm" + counter,
                                Car.builder()
                                   .withName("bwm" + counter)
                                   .withHp(180)
                                   .withYear(2016)
                                   .withModel("5")
                                   .withLocation("istanbul")
                                   .build());
//                        synchronized (object) {
//                            Thread.sleep(1_000);
//                        }
                        // Thread.sleep(0, 1_000);
                    }
                    if (counter % 100_000 == 0) {
                        System.out.println("%%%%%%% SIZE %%%%% : " + map.size());
                    }
//                    if (map.size() > 4_000_000) {
//                        map.clear();
//                    }
                    if (map.size() > 100_000) {
                        try {
                            Thread.sleep(100_000);
                        } catch (Exception exp) {

                        }
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }

        public void testPerformance(String test,
                                    long wait) {
            String str = UUID.randomUUID()
                             .toString() + test;
            try {
                Thread.sleep(10);
            } catch (Exception exp) {
            }
            abcPerformance(test,wait);
        }

        public void abcPerformance(String test,
                                    long wait) {
            String str = UUID.randomUUID()
                             .toString() + test;
            try {
                Thread.sleep(20);
            } catch (Exception exp) {
            }
            xyzPerformance(test, wait);
        }

        public void xyzPerformance(String test,
                                   long wait) {
            String str = UUID.randomUUID()
                             .toString() + test;
            try {
                Thread.sleep(25);
            } catch (Exception exp) {
            }
        }

    }

}
