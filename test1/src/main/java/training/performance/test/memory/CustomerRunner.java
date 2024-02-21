package training.performance.test.memory;

import java.util.Scanner;

public class CustomerRunner {
    public static CustomerCache customerCache = new CustomerCache();

    public static void main(String[] args) {

        Scanner scannerLoc = new Scanner(System.in);
        System.out.println("kac tane yaratayım :");
        int iLoc = scannerLoc.nextInt();
        for (int i = 0; i < iLoc; i++) {
            customerCache.add("user" + i,
                              new Customer("osman" + i,
                                           "yaycioglu",
                                           i));
        }
        for (int i = 0; i < 5; i++) {
            ProcessTread processTreadLoc = new ProcessTread();
            processTreadLoc.setName("PT-"+i);
            processTreadLoc.start();
        }
        try {
            Thread.sleep(1_000_000_000L);
        } catch (Exception exp) {
        }

    }

    public static class ProcessTread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1);
                    customerCache.method1();
                } catch (Exception exp) {
                }

            }
        }
    }

}
