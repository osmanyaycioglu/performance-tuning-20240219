package com.adenon.api.smpp.sdk;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GsmAlphabets {

    public static final Set<Integer> GSM7;
    public static final Set<Integer> GSM7_EXTENSION;
    public static final Set<Integer> GSM7_WITH_EXTENSION;
    public static final Set<Integer> SINGLE_SHIFT_TR_EXTENSION;
    public static final Set<Integer> SINGLE_SHIFT_TR_EXTENSION_LETTERS;
    public static final Set<Integer> GSM7_WITH_SINGLE_SHIFT_TR;

    static {
        GSM7 = GsmAlphabets.createGSM7Alphabet();
        GSM7_EXTENSION = GsmAlphabets.createGSM7Extension();
        GSM7_WITH_EXTENSION = GsmAlphabets.createGSM7WithExtension();
        SINGLE_SHIFT_TR_EXTENSION_LETTERS = GsmAlphabets.createSingleShiftTrExtensionLetters();
        SINGLE_SHIFT_TR_EXTENSION = GsmAlphabets.createSingleShiftTrExtension();
        GSM7_WITH_SINGLE_SHIFT_TR = GsmAlphabets.createGSM7ExtWithSingleShiftTr();
    }

    private static Set<Integer> createGSM7ExtWithSingleShiftTr() {
        final HashSet<Integer> set = new HashSet<Integer>(144);
        set.addAll(GsmAlphabets.GSM7_WITH_EXTENSION);
        set.addAll(GsmAlphabets.SINGLE_SHIFT_TR_EXTENSION);

        return Collections.unmodifiableSet(set);
    }

    private static Set<Integer> createGSM7WithExtension() {
        final HashSet<Integer> set = new HashSet<Integer>(137);
        set.addAll(GsmAlphabets.GSM7);
        set.addAll(GsmAlphabets.GSM7_EXTENSION);

        return Collections.unmodifiableSet(set);
    }

    private static Set<Integer> createSingleShiftTrExtension() {
        final HashSet<Integer> set = new HashSet<Integer>(17);
        set.addAll(GsmAlphabets.SINGLE_SHIFT_TR_EXTENSION_LETTERS);
        set.addAll(GsmAlphabets.GSM7_EXTENSION);

        return Collections.unmodifiableSet(set);
    }

    private static Set<Integer> createSingleShiftTrExtensionLetters() {
        final HashSet<Integer> set = new HashSet<Integer>(17);
        set.add(350);
        set.add(231);
        set.add(351);
        set.add(286);
        set.add(287);
        set.add(304);
        set.add(305);

        return Collections.unmodifiableSet(set);
    }

    private static Set<Integer> createGSM7Extension() {
        final HashSet<Integer> set = new HashSet<Integer>(10);
        set.add(124);
        set.add(94);
        set.add(8364);
        set.add(123);
        set.add(125);
        set.add(12);
        set.add(91);
        set.add(126);
        set.add(93);
        set.add(92);

        return Collections.unmodifiableSet(set);
    }

    private static Set<Integer> createGSM7Alphabet() {
        final HashSet<Integer> set = new HashSet<Integer>(127);
        set.add(64);
        set.add(163);
        set.add(36);
        set.add(165);
        set.add(232);
        set.add(233);
        set.add(249);
        set.add(236);
        set.add(242);
        set.add(199);
        set.add(10);
        set.add(216);
        set.add(248);
        set.add(13);
        set.add(197);
        set.add(229);

        set.add(916);
        set.add(95);
        set.add(934);
        set.add(915);
        set.add(923);
        set.add(937);
        set.add(928);
        set.add(936);
        set.add(931);
        set.add(920);
        set.add(926);
        set.add(27);
        set.add(198);
        set.add(230);
        set.add(223);
        set.add(201);

        set.add(32);
        set.add(33);
        set.add(34);
        set.add(35);
        set.add(164);
        for (int i = 37; i <= 63; i++) {
            set.add(i);
        }
        set.add(161);

        for (int i = 65; i <= 90; i++) {
            set.add(i);
        }
        set.add(196);
        set.add(214);
        set.add(209);
        set.add(220);
        set.add(167);

        set.add(191);
        for (int i = 97; i <= 122; i++) {
            set.add(i);
        }
        set.add(228);
        set.add(246);
        set.add(241);
        set.add(252);
        set.add(224);

        return Collections.unmodifiableSet(set);
    }
}
