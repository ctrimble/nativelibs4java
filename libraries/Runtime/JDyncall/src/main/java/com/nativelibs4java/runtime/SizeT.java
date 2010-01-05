/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nativelibs4java.runtime;

/**
 * Wraps a value which size is the same as the 'size_t' C type (32 bits on a 32 bits platform, 64 bits on a 64 bits platform)
 * @author Olivier
 */
public class SizeT extends Number {
    private final long value;
    public SizeT(long value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE)
            throw new RuntimeException("Value is not within the int range");
        return (int)value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
}