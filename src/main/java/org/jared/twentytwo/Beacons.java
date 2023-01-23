package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple4;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Beacons implements Runnable {
    public static void main(String... args) {
        new Beacons().run();
    }

    private static final Pattern p =
            Pattern.compile("Sensor at x=([0-9\\-]+), y=([0-9\\-]+): closest beacon is at x=([0-9\\-]+), y=([0-9\\-]+)");

    private static class ScanIterator implements Iterator<String> {
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

    @Override
    public void run() {
        Scanner sc = new Scanner(Beacons.class.getResourceAsStream("/2022/day15_input.txt"));

        ScanIterator iter = new ScanIterator(sc);

        List<Tuple4<Integer, Integer, Integer, Integer>> allData = iter.map(nextLine -> {
            Matcher m = p.matcher(nextLine);
            m.find();
            return Tuple.of(m.group(1), m.group(2), m.group(3), m.group(4))
                    .map(Integer::parseInt, Integer::parseInt, Integer::parseInt, Integer::parseInt);
        }).toList();

        var beacons = allData.map(t -> Tuple.of(t._3(), t._4()));

        for(int i = 3070641; i < 4_000_000; i++) {
            final int current = i;
            TreeMap<Integer, Integer> segmentMap =
                    allData.flatMap(tuple -> compute(tuple, current)).foldLeft(new TreeMap<>(), this::addTupleToMap);

            if (segmentMap.size() > 1) {
                var tup = Tuple.of(segmentMap.lastKey() - 1, i);
                if (!beacons.contains(tup)) {
                    System.out.println(segmentMap);
                    System.out.println(tup);
                    System.out.println(((long) tup._1()) * 4_000_000l + ((long) tup._2()));
                    break;
                }
            }
        }

    }

    private TreeMap<Integer, Integer> addTupleToMap(TreeMap<Integer, Integer> currentMap,
                                                    Tuple2<Integer, Integer> newRange) {
        var belowEntry = currentMap.floorKey(newRange._1());
        var subMap = currentMap.subMap(belowEntry == null ? newRange._1() : belowEntry, true, newRange._2(), true);
        var mapIter = subMap.entrySet().iterator();

        int smallest = newRange._1();
        int biggest = newRange._2();

        while(mapIter.hasNext()) {
            Map.Entry<Integer, Integer> nextEntry = mapIter.next();
            if ((biggest >= nextEntry.getKey() && biggest <= nextEntry.getValue()) ||
                    (nextEntry.getValue() >= smallest) && (nextEntry.getValue() <= biggest)) {
                smallest = Math.min(smallest, nextEntry.getKey());
                biggest = Math.max(biggest, nextEntry.getValue());
                mapIter.remove();
            }
        }

        currentMap.put(smallest, biggest);

        return currentMap;
    }

    private Option<Tuple2<Integer, Integer>> compute(Tuple4<Integer, Integer, Integer, Integer> tuple, int target) {
        int yRemaining = tuple._2() - target;

        int xDistance = Math.abs(tuple._1() - tuple._3());
        int yDistance = Math.abs(tuple._2() - tuple._4());
        int total = xDistance + yDistance;

        int diff = total - Math.abs(yRemaining);

        if (diff <= 0) return Option.none();

        int smallDiff = tuple._1() - diff;
        int bigDiff = tuple._1() + diff;

        if (smallDiff == tuple._3()) smallDiff++;
        if (bigDiff == tuple._3()) bigDiff--;

        return Option.some(Tuple.of(smallDiff, bigDiff));
    }
}
