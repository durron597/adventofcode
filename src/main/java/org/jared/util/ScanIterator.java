package org.jared.util;

import io.vavr.collection.Iterator;

import java.util.Scanner;

public class ScanIterator implements Iterator<String> {
    private final Scanner sc;

    public ScanIterator(Scanner sc) {
        this.sc = sc;
    }

    @Override
    public boolean hasNext() {
        return sc.hasNextLine();
    }

    @Override
    public String next() {
        return sc.nextLine();
    }
}
