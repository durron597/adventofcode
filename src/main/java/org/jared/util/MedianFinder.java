package org.jared.util;

import java.util.Map;
import java.util.TreeMap;

public class MedianFinder {
    TreeMap<Long, Long> less = new TreeMap<>();
    TreeMap<Long, Long> more = new TreeMap<>();
    long lessSize = 0;
    long moreSize = 0;
    long currentMedian = -1;
    long size = 0;

    public MedianFinder() {
    }

    public void addNum(long num) {
        if (size == 0) {
            currentMedian = num;
        } else if (num < currentMedian) {
            less.put(num, less.getOrDefault(num, 0L) + 1);
            lessSize++;
        } else {
            more.put(num, more.getOrDefault(num, 0L) + 1);
            moreSize++;
        }
        balance();
        size++;
    }

    private void balance() {
        // System.out.format("Balance|less:%s,lessSize:%d,currentMedian:%s,more:%s,moreSize:%d%n", render(less), lessSize, currentMedian, render(more), moreSize);
        if (lessSize - moreSize > 1) {
            more.put(currentMedian, more.getOrDefault(currentMedian, 0L) + 1);
            moreSize++;
            Map.Entry<Long, Long> lastEntry = less.lastEntry();
            if(lastEntry.getValue() == 1) {
                less.pollLastEntry();
            } else {
                less.put(lastEntry.getKey(), lastEntry.getValue() - 1);
            }
            lessSize--;
            currentMedian = lastEntry.getKey();
        } else if (moreSize - lessSize > 1) {
            less.put(currentMedian, less.getOrDefault(currentMedian, 0L) + 1);
            lessSize++;
            Map.Entry<Long, Long> firstEntry = more.firstEntry();
            if(firstEntry.getValue() == 1) {
                more.pollFirstEntry();
            } else {
                more.put(firstEntry.getKey(), firstEntry.getValue() - 1);
            }
            moreSize--;
            currentMedian = firstEntry.getKey();
        }
    }

    public double findMedian() {
        // System.out.format("FindMedian|less:%s,lessSize:%d,currentMedian:%s,more:%s,moreSize:%d%n", render(less), lessSize, currentMedian, render(more), moreSize);
        if(lessSize == moreSize) {
            return currentMedian;
        } else if (lessSize > moreSize) {
            return (currentMedian + less.lastKey()) / 2.;
        } else {
            return (currentMedian + more.firstKey()) / 2.;
        }

    }

    private String render(TreeMap<Long, Long> map) {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        map.forEach((k, v) -> {
            for(long i = 0; i < v; i++) {
                sb.append(k);
                sb.append(',');
            }
        });
        sb.append(']');
        return sb.toString();
    }
}