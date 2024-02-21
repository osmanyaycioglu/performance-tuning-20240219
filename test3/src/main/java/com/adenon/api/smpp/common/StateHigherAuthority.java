package com.adenon.api.smpp.common;

import java.util.ArrayList;


public class StateHigherAuthority extends State {

    private final ArrayList<State> stateList = new ArrayList<State>();

    public StateHigherAuthority() {

    }

    public void addState(final State state) {
        this.stateList.add(state);
        state.setParent(this);
    }

    private EState getMyState() {
        int lastVal = EState.STOPPED.getEnumIntVal();
        for (final State state : this.stateList) {
            lastVal |= state.getMyCurrentState().getEnumIntVal();
        }
        if (lastVal == EState.STOPPED.getEnumIntVal()) {
            return EState.STOPPED;
        } else if (lastVal == EState.SUSPENED.getEnumIntVal()) {
            return EState.SUSPENED;
        } else {
            return EState.IDLE;
        }
    }

    @Override
    public synchronized void idle() {
        this.myCurrentState = this.getMyState();
        if (this.parent != null) {
            this.parent.idle();
        }
        synchronized (this.notifyObject) {
            this.notifyObject.notifyAll();
        }
    }

    @Override
    public synchronized void stopped() {
        this.myCurrentState = this.getMyState();
        if (this.parent != null) {
            this.parent.stopped();
        }
        synchronized (this.notifyObject) {
            this.notifyObject.notifyAll();
        }
    }

    @Override
    public void suspended() {
        this.myCurrentState = this.getMyState();
        if (this.parent != null) {
            this.parent.suspended();
        }
        synchronized (this.notifyObject) {
            this.notifyObject.notifyAll();
        }
    }

    public static void main(final String[] args) {
        final StateHigherAuthority myOt = new StateHigherAuthority();
        final State state1 = new State();
        final State state2 = new State();
        final State state3 = new State();
        myOt.addState(state1);
        myOt.addState(state2);
        myOt.addState(state3);
        state1.idle();
        state1.stopped();
        state1.suspended();
        state2.idle();
        state3.idle();
        System.out.println(myOt.toString());


    }
}
