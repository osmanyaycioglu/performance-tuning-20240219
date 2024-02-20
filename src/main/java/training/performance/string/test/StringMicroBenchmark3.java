package training.performance.string.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StringMicroBenchmark3 {

    public static List<String> listLoc = new LinkedList<>();
    public static List<String> temp    = new ArrayList<>(5_200_000);


    public static void main(String[] args) {
        int i1 = 100;
        for (int i = 0; i < 100_000; i++) {
            String str = "osman" + i;
            listLoc.add(str);
        }
        listLoc.clear();
        try {
            Thread.sleep(1_000);
        } catch (Exception exp) {
        }
        System.gc();
        try {
            Thread.sleep(1_000);
        } catch (Exception exp) {
        }

        long lastClear = System.currentTimeMillis();
        while (true) {
            long delta = System.currentTimeMillis();
            for (int i = 0; i < 1_000_000; i++) {
                temp.add("osman" + i);
            }
            try {
                Thread.sleep(10);
            } catch (Exception exp) {
            }
            if (temp.size() > 5_000_000){
                temp.clear();
            }

            if (listLoc.size() < 10_000_000) {
                for (int i = 0; i < 500_000; i++) {
                    listLoc.add("osman" + i);
                }
            } else {
                if (System.currentTimeMillis() - lastClear > 120_000) {
                    for (int i = 0; i < 5_000_000; i++) {
                        listLoc.remove(0);
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (Exception exp) {
            }
        }

    }
}
