package com.adenon.library.common.utils;

import java.util.Random;


public class TraceIdGenerator {

    public static String generateTraceId(final int headerLength) {
        final Random random = new Random();
        final int nextInt = random.nextInt(TraceIdGenerator.charArray.length);
        final int seed = random.nextInt(8);
        final char[] traceChars = new char[headerLength];
        int counter = nextInt;
        for (int i = 0; i < headerLength; i++) {
            traceChars[i] = TraceIdGenerator.charArray[counter];
            counter += 1 + seed;
            counter %= TraceIdGenerator.charArray.length;
        }
        final StringBuilder builder = new StringBuilder(20 + headerLength);
        builder.append(traceChars);
        builder.append(System.currentTimeMillis());
        return builder.toString();

    }

    public static void main(final String[] args) {
        final long delta = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            TraceIdGenerator.generateTraceId(8);
            // System.out.println(generateTraceId(4));
        }
        System.out.println("Delta : " + (System.currentTimeMillis() - delta));

    }

    private final static char[] charArray = {
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'r',
            's',
            't',
            'u',
            'v',
            'y',
            'z',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'R',
            'S',
            'T',
            'U',
            'V',
            'Y',
            'Z',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9'                          };

}
