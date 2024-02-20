package training.performance.string.test;

import java.util.ArrayList;
import java.util.List;

public class StringMicroBenchmark {

    public static void main(String[] args) {
        List<String> listLoc = new ArrayList<>(1_200_000);
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



        long delta = System.currentTimeMillis();
        for (int i = 0; i < 10_000_000; i++) {
            // String stringLoc = "For döngüsü : " + i + " başladı. sonuç : osman" + i;
            listLoc.add("osman" + i);
        }
        System.out.println("Delta : " + (System.currentTimeMillis() - delta));
        try {
            Thread.sleep(1_000);
        } catch (Exception exp) {
        }
        System.gc();
        try {
            Thread.sleep(1_000);
        } catch (Exception exp) {
        }

    }
}
