package org.jared.mix;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jared.util.ScanIterator;

import java.util.Scanner;

public class Mixer {
    @Data
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Container implements Identifiable {
        int id;

        long data;
    }

    public static final boolean DEBUG = false;

    public static void main (String... args){

        long start = System.nanoTime();

        long[] fileInts;

        if (DEBUG) {
            fileInts = new long[]{1, 2, -3, 3, -2, 0, 4};
        } else {
            ScanIterator sc = new ScanIterator(new Scanner(Mixer.class.getResourceAsStream("/2022/day20_input.txt")));
            fileInts = sc.map(Integer::parseInt).toJavaStream().mapToLong(i -> i).toArray();
        }

        ScapeGoatTree<Container> sgt = new ScapeGoatTree<>();
        List<Container> containerList = List.empty();

        for(int i = fileInts.length - 1; i >= 0; i--) {
            Container entryContainer = new Container(i * 65536 + 524288, fileInts[i] * 811589153);
            sgt.insert(entryContainer);
            containerList = containerList.prepend(entryContainer);
        }

        for (int i = 0; i < 10; i++) {
            if (DEBUG) {
                System.out.format("After %d rounds of mixing:%n", i);
                boolean first = true;
                for (var e : sgt) {
                    if (!first) System.out.print(", ");
                    first = false;
                    System.out.print(e.getData());
                }
                System.out.println();
                System.out.println();
            }

            for (var con : containerList) {
                int rank;
                Container lesser;
                Container greater;
                int newId;
                if (con.getData() == 0) {
//                    if (DEBUG) System.out.println("0 does not move:");
                } else {
                    while (true) {
                        rank = sgt.rank(con);
                        int moduloRank = (int) (rank + (posMod(con.getData(), containerList.size() - 1))) % (containerList.size());
                        if (moduloRank < 0) {
                            moduloRank += (containerList.size() - 1);
                        }
                        lesser = sgt.kthSmallest(moduloRank);
                        if (moduloRank + 1 == containerList.size()) {
                            newId = lesser.getId() + 65536;
//                            System.out.println(newId);
//                            if (DEBUG)
//                                System.out.format("%d (%d) moves between %d and end:%n",
//                                        con.getData(), (posMod(con.getData(), containerList.size() - 1)), lesser.getData());
                            break;
                        }
                        int nextBiggerRank = (moduloRank + 1) % (containerList.size());
                        greater = sgt.kthSmallest(nextBiggerRank);
//                        if (DEBUG)
//                            System.out.format("%d (%d) moves between %d and %d:%n", con.getData(), (posMod(con.getData(), containerList.size() - 1)), lesser.getData(), greater.getData());
                        int diff = (greater.getId() - lesser.getId()) / 2;
//                        System.out.println(diff);
                        if (diff > 0) {
                            newId = diff + lesser.getId();
                            break;
                        }
                        int rebalancedIdBase = 0;
                        for(var sgtCon : sgt) {
                            sgtCon.setId(rebalancedIdBase * 65536 + 524288);
                            rebalancedIdBase++;
                        }
                    }
                    sgt.remove(con.getId());
                    con.setId(newId);
                    sgt.insert(con);
                }
//                if (DEBUG) {
//                    boolean first = true;
//                    for (var e : sgt) {
//                        if (!first) System.out.print(", ");
//                        first = false;
//                        System.out.print(e.getData());
//                    }
//                    System.out.println();
//                    System.out.println();
//                }
            }
        }

        if (DEBUG) {
            System.out.format("After %d rounds of mixing:%n", 10);
            boolean first = true;
            for (var e : sgt) {
                if (!first) System.out.print(", ");
                first = false;
                System.out.print(e.getData());
            }
            System.out.println();
            System.out.println();
        }

        List<Container> list = List.ofAll(sgt);
        int indexOfZero = list.zipWithIndex().find(t -> t._1().getData() == 0).get()._2();
        long zero = list.get((indexOfZero) % list.size()).getData();
        long first = list.get((indexOfZero + 1000) % list.size()).getData();
        long second = list.get((indexOfZero + 2000) % list.size()).getData();
        long third = list.get((indexOfZero + 3000) % list.size()).getData();

        System.out.format("zero: %d, first: %d, second: %d, third: %d, sum: %d%n", zero, first, second, third, first+second+third);

        long stop = System.nanoTime();

        double elapsed = ((double) (stop - start)) / (1_000_000_000.);

        System.out.println("Elapsed in seconds: " + elapsed);
    }

    private static long posMod(long divisor, long modulus) {
        if (modulus < 0) return posMod(-divisor, -modulus);

        long vanilla = divisor % modulus;

        if (vanilla < 0) return vanilla + modulus;

        return vanilla;
    }
}
