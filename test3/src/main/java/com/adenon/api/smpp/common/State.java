package com.adenon.api.smpp.common;

public class State {

    protected EState       myCurrentState = EState.STOPPED;
    protected final Object notifyObject;
    protected State        parent;

    public State() {
        this.notifyObject = new Object();
    }

    public boolean waitIdle() {
        while (this.myCurrentState != EState.IDLE) {
            try {
                synchronized (this.notifyObject) {
                    this.notifyObject.wait();
                }
            } catch (final Exception e) {
                return false;
            }
        }
        return (this.myCurrentState == EState.IDLE);
    }

    public boolean waitStopped() {
        while (this.myCurrentState != EState.STOPPED) {
            try {
                synchronized (this.notifyObject) {
                    this.notifyObject.wait();
                }
            } catch (final Exception e) {
                return false;
            }
        }
        return (this.myCurrentState == EState.STOPPED);
    }

    public boolean waitIdle(final long timeout) {
        final long startTime = System.currentTimeMillis();
        long current = startTime;
        while ((this.myCurrentState != EState.IDLE) && (current < (startTime + timeout))) {
            try {
                synchronized (this.notifyObject) {
                    this.notifyObject.wait(timeout);
                }
            } catch (final Exception e) {
                return false;
            }
            current = System.currentTimeMillis();
        }
        return (this.myCurrentState == EState.IDLE);
    }

    public boolean waitStopped(final long timeout) {
        final long startTime = System.currentTimeMillis();
        long current = startTime;
        while ((this.myCurrentState != EState.STOPPED) && (current < (startTime + timeout))) {
            try {
                synchronized (this.notifyObject) {
                    this.notifyObject.wait(timeout);
                }
            } catch (final Exception e) {
                return false;
            }
            current = System.currentTimeMillis();
        }
        return (this.myCurrentState == EState.STOPPED);
    }

    public synchronized void idle() {
        this.myCurrentState = EState.IDLE;
        if (this.parent != null) {
            this.parent.idle();
        }
        synchronized (this.notifyObject) {
            this.notifyObject.notifyAll();
        }
    }

    public synchronized void stopped() {
        this.myCurrentState = EState.STOPPED;
        if (this.parent != null) {
            this.parent.stopped();
        }
        synchronized (this.notifyObject) {
            this.notifyObject.notifyAll();
        }
    }

    public synchronized void suspended() {
        this.myCurrentState = EState.SUSPENED;
        if (this.parent != null) {
            this.parent.suspended();
        }
        synchronized (this.notifyObject) {
            this.notifyObject.notifyAll();
        }
    }

    public boolean isIdle() {
        return ((this.myCurrentState == EState.IDLE));
    }

    public boolean isSuspended() {
        return (this.myCurrentState == EState.SUSPENED);
    }

    public boolean isStopped() {
        return (this.myCurrentState == EState.STOPPED);
    }

    @Override
    public String toString() {
        switch (this.myCurrentState) {
            case IDLE:
                return "Idle";
            case SUSPENED:
                return "Suspended";
            case STOPPED:
                return "Stopped";
            default:
                break;
        }
        return "Unknown";
    }

    public State getParent() {
        return this.parent;
    }

    public void setParent(final State parent) {
        this.parent = parent;
    }

    public EState getMyCurrentState() {
        return this.myCurrentState;
    }

}
