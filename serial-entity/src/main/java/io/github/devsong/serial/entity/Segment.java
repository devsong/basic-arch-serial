package io.github.devsong.serial.entity;

import java.util.concurrent.atomic.AtomicLong;

public class Segment {
    private AtomicLong value = new AtomicLong(0);
    private volatile long max;
    private volatile int step;
    private volatile int randomLen;
    private final SegmentBuffer buffer;

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    public AtomicLong getValue() {
        return value;
    }

    public void setValue(AtomicLong value) {
        this.value = value;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getRandomLen() {
        return randomLen;
    }

    public void setRandomLen(int randomLen) {
        this.randomLen = randomLen;
    }

    public SegmentBuffer getBuffer() {
        return buffer;
    }

    public long getIdle() {
        return this.getMax() - getValue().get();
    }

    @Override
    public String toString() {
        String sb = "Segment(" + "value:" +
                value +
                ",max:" +
                max +
                ",step:" +
                step +
                ")";
        return sb;
    }
}
