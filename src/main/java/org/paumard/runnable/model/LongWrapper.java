package org.paumard.runnable.model;

public class LongWrapper {

    private long l;
    private Object key = new Object();

    public LongWrapper(long l){
        this.l = l;
    }

    public long getValue() {
        synchronized (key) {
            return l;
        }
    }

    public void incrementValue(){
        synchronized (key){
            l = l + 1;
        }
    }
}
