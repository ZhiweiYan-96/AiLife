package com.record.view.wheel.widget;

public class ArrayWheelAdapter<T> implements WheelAdapter {
    public static final int DEFAULT_LENGTH = -1;
    private T[] items;
    private int length;

    public ArrayWheelAdapter(T[] items, int length) {
        this.items = items;
        this.length = length;
    }

    public ArrayWheelAdapter(T[] items) {
        this(items, -1);
    }

    public String getItem(int index) {
        if (index < 0 || index >= this.items.length) {
            return null;
        }
        return this.items[index].toString();
    }

    public int getItemsCount() {
        return this.items.length;
    }

    public int getMaximumLength() {
        return this.length;
    }
}
