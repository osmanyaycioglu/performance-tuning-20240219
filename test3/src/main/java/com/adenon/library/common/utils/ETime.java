package com.adenon.library.common.utils;


public enum ETime {
    YEAR {

        @Override
        public long getMiliseconds(final long unit) {
            return ETime.YEARS * unit;
        }

        @Override
        public long getMiliseconds() {
            return ETime.YEARS;
        }

    },
    MONTH {

        @Override
        public long getMiliseconds(final long unit) {
            return ETime.MONTHS * unit;
        }

        @Override
        public long getMiliseconds() {
            return ETime.MONTHS;
        }

    },
    DAY {

        @Override
        public long getMiliseconds(final long unit) {
            return ETime.DAYS * unit;
        }

        @Override
        public long getMiliseconds() {
            return ETime.DAYS;
        }

    },
    HOUR {

        @Override
        public long getMiliseconds(final long unit) {
            return ETime.HOURS * unit;
        }

        @Override
        public long getMiliseconds() {
            return ETime.HOURS;
        }

    },
    MINUTE {

        @Override
        public long getMiliseconds(final long unit) {
            return ETime.MINUTES * unit;
        }

        @Override
        public long getMiliseconds() {
            return ETime.MINUTES;
        }


    },
    SECOND {

        @Override
        public long getMiliseconds(final long unit) {
            return ETime.SECONDS * unit;
        }

        @Override
        public long getMiliseconds() {
            return ETime.SECONDS;
        }

    };

    private static final long MILISECONDS = 1;
    private static final long SECONDS     = 1000 * ETime.MILISECONDS;
    private static final long MINUTES     = 60 * ETime.SECONDS;
    private static final long HOURS       = 60 * ETime.MINUTES;
    private static final long DAYS        = 24 * ETime.HOURS;
    private static final long MONTHS      = 30 * ETime.DAYS;
    private static final long YEARS       = 12 * ETime.MONTHS;

    public long getMiliseconds(final long unit) {
        throw new AbstractMethodError();
    }

    public long getMiliseconds() {
        throw new AbstractMethodError();
    }

    public long getRemainingMiliseconds(final long miliseconds) {
        return miliseconds % this.getMiliseconds();
    }

    public int getUnit(final long miliseconds) {
        if (miliseconds < this.getMiliseconds()) {
            return 0;
        }
        return (int) (miliseconds / this.getMiliseconds());
    }

    public String convertToString(final int unit) {
        final StringBuilder strBuild = new StringBuilder(6);
        if (unit < 10) {
            strBuild.append("0");
        }
        strBuild.append(unit);
        return strBuild.toString();
    }

    public void convertToString(final int unit,
                                final StringBuilder strBuild) {
        if (unit < 10) {
            strBuild.append("0");
        }
        strBuild.append(unit);
    }

}
