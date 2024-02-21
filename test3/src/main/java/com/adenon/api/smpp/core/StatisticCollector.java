package com.adenon.api.smpp.core;

import com.adenon.api.smpp.common.StatisticCounter;

public class StatisticCollector {

    private final StatisticCounter totalReceivedPackageCount              = new StatisticCounter(0);
    private final StatisticCounter totalReceivedSubmitSMCount             = new StatisticCounter(0);
    private final StatisticCounter totalReceivedSuccessfullSubmitSMCount  = new StatisticCounter(0);
    private final StatisticCounter totalReceivedFailedSubmitSMCount       = new StatisticCounter(0);
    private final StatisticCounter totalReceivedRetrySubmitSMCount        = new StatisticCounter(0);

    private final StatisticCounter totalReceivedSuccessfullDeliverSMCount = new StatisticCounter(0);
    private final StatisticCounter totalReceivedFailedDeliverSMCount      = new StatisticCounter(0);
    private final StatisticCounter totalReceivedRetryDeliverSMCount       = new StatisticCounter(0);

    private final StatisticCounter totalReceivedDeliveryCount             = new StatisticCounter(0);
    private final StatisticCounter totalReceivedDeliverSMCount            = new StatisticCounter(0);
    private final StatisticCounter totalSentPackageCount                  = new StatisticCounter(0);
    private final StatisticCounter totalSentSubmitSM                      = new StatisticCounter(0);
    private final StatisticCounter totalBufferMissedCount                 = new StatisticCounter(0);
    private final StatisticCounter totalReconnectionToHost                = new StatisticCounter(0);
    private final StatisticCounter totalHostConnectionClose               = new StatisticCounter(0);

    private final StatisticCounter totalReceivedSubmitMultiSMCount        = new StatisticCounter(0);
    private final StatisticCounter totalReceivedFailedSubmitMultiSMCount  = new StatisticCounter(0);
    private final StatisticCounter totalReceivedRetrySubmitMultiSMCount   = new StatisticCounter(0);

    private final StatisticCounter totalReceivedSuccessfullQuerySMCount   = new StatisticCounter(0);
    private final StatisticCounter totalReceivedFailedQuerySMCount        = new StatisticCounter(0);
    private final StatisticCounter totalReceivedRetryQuerySMCount         = new StatisticCounter(0);


    public void increaseTotalReceivedPackageCount() {
        this.totalReceivedPackageCount.increaseCounter();
    }

    public void increaseTotalReceivedSubmitSMCount() {
        this.totalReceivedSubmitSMCount.increaseCounter();
    }

    public void increaseTotalReceivedSuccessfullSubmitSMCount() {
        this.totalReceivedSuccessfullSubmitSMCount.increaseCounter();
    }

    public void increaseTotalReceivedFailedSubmitSMCount() {
        this.totalReceivedFailedSubmitSMCount.increaseCounter();
    }

    public void increaseTotalReceivedRetrySubmitSMCount() {
        this.getTotalReceivedRetrySubmitSMCount().increaseCounter();
    }

    public void increaseTotalReceivedDeliveryCount() {
        this.totalReceivedDeliveryCount.increaseCounter();
    }

    public void increaseTotalReceivedDeliverSMCount() {
        this.totalReceivedDeliverSMCount.increaseCounter();
    }

    public void increaseTotalSentPackageCount() {
        this.totalSentPackageCount.increaseCounter();
    }

    public void increaseTotalSentPackageCount(final int value) {
        this.totalSentPackageCount.increaseCounter(value);
    }

    public void increaseTotalSentSubmitSM() {
        this.totalSentSubmitSM.increaseCounter();
    }

    public void increaseTotalBufferMissedCount() {
        this.totalBufferMissedCount.increaseCounter();
    }

    public void increaseTotalReconnectionToHost() {
        this.totalReconnectionToHost.increaseCounter();
    }

    public void increaseTotalHostConnectionClose() {
        this.totalHostConnectionClose.increaseCounter();
    }

    public void resetCounters() {
        this.totalReceivedSubmitSMCount.resetCounter();
        this.totalReceivedPackageCount.resetCounter();
        this.totalReceivedSuccessfullSubmitSMCount.resetCounter();
        this.totalReceivedFailedSubmitSMCount.resetCounter();
        this.getTotalReceivedRetrySubmitSMCount().resetCounter();
        this.totalReceivedDeliveryCount.resetCounter();
        this.totalReceivedDeliverSMCount.resetCounter();
        this.totalSentPackageCount.resetCounter();
        this.totalSentSubmitSM.resetCounter();
        this.totalBufferMissedCount.resetCounter();
        this.totalReconnectionToHost.resetCounter();
        this.totalHostConnectionClose.resetCounter();
        this.totalReceivedRetryDeliverSMCount.resetCounter();
        this.totalReceivedSubmitMultiSMCount.resetCounter();
        this.totalReceivedFailedSubmitMultiSMCount.resetCounter();
        this.totalReceivedRetrySubmitMultiSMCount.resetCounter();
        this.totalReceivedSuccessfullQuerySMCount.resetCounter();
        this.totalReceivedFailedQuerySMCount.resetCounter();
        this.totalReceivedRetryQuerySMCount.resetCounter();
    }

    public StatisticCounter getTotalReceivedPackageCount() {
        return this.totalReceivedPackageCount;
    }

    public StatisticCounter getTotalReceivedSubmitSMCount() {
        return this.totalReceivedSubmitSMCount;
    }

    public StatisticCounter getTotalReceivedSuccessfullSubmitSMCount() {
        return this.totalReceivedSuccessfullSubmitSMCount;
    }

    public StatisticCounter getTotalReceivedFailedSubmitSMCount() {
        return this.totalReceivedFailedSubmitSMCount;
    }

    public StatisticCounter getTotalReceivedRetrySubmitSMCount() {
        return this.totalReceivedRetrySubmitSMCount;
    }

    public StatisticCounter getTotalReceivedDeliveryCount() {
        return this.totalReceivedDeliveryCount;
    }

    public StatisticCounter getTotalReceivedDeliverSMCount() {
        return this.totalReceivedDeliverSMCount;
    }

    public StatisticCounter getTotalSentPackageCount() {
        return this.totalSentPackageCount;
    }

    public StatisticCounter getTotalSentSubmitSM() {
        return this.totalSentSubmitSM;
    }

    public StatisticCounter getTotalBufferMissedCount() {
        return this.totalBufferMissedCount;
    }

    public StatisticCounter getTotalReconnectionToHost() {
        return this.totalReconnectionToHost;
    }

    public StatisticCounter getTotalHostConnectionClose() {
        return this.totalHostConnectionClose;
    }

    @Override
    public String toString() {
        return "StatisticCollector [totalReceivedPackageCount="
               + this.totalReceivedPackageCount
               + ", totalReceivedSubmitSMCount="
               + this.totalReceivedSubmitSMCount
               + ", totalReceivedSuccessfullSubmitSMCount="
               + this.totalReceivedSuccessfullSubmitSMCount
               + ", totalReceivedFailedSubmitSMCount="
               + this.totalReceivedFailedSubmitSMCount
               + ", totalReceivedRetrySubmitSMCount="
               + this.totalReceivedRetrySubmitSMCount
               + ", totalReceivedDeliveryCount="
               + this.totalReceivedDeliveryCount
               + ", totalReceivedDeliverSMCount="
               + this.totalReceivedDeliverSMCount
               + ", totalReceivedSuccessfullDeliverSMCount="
               + this.totalReceivedSuccessfullDeliverSMCount
               + ", totalReceivedFailedDeliverSMCount="
               + this.totalReceivedFailedDeliverSMCount
               + ", totalReceivedRetryDeliverSMCount="
               + this.totalReceivedRetryDeliverSMCount
               + ", totalSentPackageCount="
               + this.totalSentPackageCount
               + ", totalSentSubmitSM="
               + this.totalSentSubmitSM
               + ", totalBufferMissedCount="
               + this.totalBufferMissedCount
               + ", totalReconnectionToHost="
               + this.totalReconnectionToHost
               + ", totalHostConnectionClose="
               + this.totalHostConnectionClose
               + "]";
    }

    public String lastValues() {
        return "StatisticCollector [ReceivedPackageCount="
               + this.totalReceivedPackageCount.getLastValue()
               + ", ReceivedSubmitSMCount="
               + this.totalReceivedSubmitSMCount.getLastValue()
               + ", ReceivedSuccessfullSubmitSMCount="
               + this.totalReceivedSuccessfullSubmitSMCount.getLastValue()
               + ", ReceivedFailedSubmitSMCount="
               + this.totalReceivedFailedSubmitSMCount.getLastValue()
               + ", ReceivedRetrySubmitSMCount="
               + this.totalReceivedRetrySubmitSMCount.getLastValue()
               + ", ReceivedDeliveryCount="
               + this.totalReceivedDeliveryCount.getLastValue()
               + ", ReceivedDeliverSMCount="
               + this.totalReceivedDeliverSMCount.getLastValue()
               + ", ReceivedSuccessfullDeliverSMCount="
               + this.totalReceivedSuccessfullDeliverSMCount.getLastValue()
               + ", ReceivedFailedDeliverSMCount="
               + this.totalReceivedFailedDeliverSMCount.getLastValue()
               + ", ReceivedRetryDeliverSMCount="
               + this.totalReceivedRetryDeliverSMCount.getLastValue()
               + ", SentPackageCount="
               + this.totalSentPackageCount.getLastValue()
               + ", SentSubmitSM="
               + this.totalSentSubmitSM.getLastValue()
               + ", BufferMissedCount="
               + this.totalBufferMissedCount.getLastValue()
               + ", ReconnectionToHost="
               + this.totalReconnectionToHost.getLastValue()
               + ", HostConnectionClose="
               + this.totalHostConnectionClose.getLastValue()
               + "]";
    }

    public void increaseTotalReceivedRetryDeliverSMCount() {
        this.totalReceivedRetryDeliverSMCount.increaseCounter();
    }

    public void increaseTotalReceivedSuccessfullDeliverSMCount() {
        this.totalReceivedSuccessfullDeliverSMCount.increaseCounter();
    }

    public void increaseTotalReceivedFailedDliverSMCount() {
        this.totalReceivedFailedDeliverSMCount.increaseCounter();

    }

    public void increaseTotalReceivedSuccessfullSubmitMultiSMCount() {
        this.totalReceivedSubmitMultiSMCount.increaseCounter();
    }

    public void increaseTotalReceivedRetrySubmitMultiSMCount() {
        this.totalReceivedRetrySubmitMultiSMCount.increaseCounter();
    }

    public void increaseTotalReceivedFailedSubmitMultiSMCount() {
        this.totalReceivedFailedSubmitMultiSMCount.increaseCounter();
    }

    public void increaseTotalReceivedSuccessfullQuerySMCount() {
        this.totalReceivedSuccessfullQuerySMCount.increaseCounter();
    }

    public void increaseTotalReceivedFailedQuerySMCount() {
        this.totalReceivedFailedQuerySMCount.increaseCounter();
    }

    public void increaseTotalReceivedRetryQuerySMCount() {
        this.totalReceivedRetryQuerySMCount.increaseCounter();
    }

}
