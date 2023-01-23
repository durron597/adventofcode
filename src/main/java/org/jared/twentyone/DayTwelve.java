package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.jared.util.Diag;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.Function;


public class DayTwelve {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayTwelve.class.getResourceAsStream("/2021/day12_input.txt")));

        var asString = iter.toSet().map(s -> s.split("-"));
        Map<String, Integer> nodeNameToId = asString.flatMap(List::of).toList()
                .sorted(Comparator.<String>comparingInt(s -> switch (s) {
                            case "start" -> 0;
                            case "end" -> 1;
                            default -> 2;
                        })
                        .thenComparing(Comparator.naturalOrder()))
                .zipWithIndex()
                .toMap(Function.identity());
        int maxUppercaseId =
                nodeNameToId.filter((s, i) -> Character.isUpperCase(s.charAt(0))).maxBy(Tuple2::_2).get()._2();
        int maxAllId = nodeNameToId.maxBy(Tuple2::_2).get()._2();
        Map<String, Set<String>> stringNeighbors = asString.foldLeft(HashMap.empty(),
                (acc, arr) -> acc.put(arr[0], acc.getOrElse(arr[0], HashSet.empty()).add(arr[1]))
                        .put(arr[1], acc.getOrElse(arr[1], HashSet.empty()).add(arr[0])));
        Map<Integer, Set<Integer>> neighbors = stringNeighbors
                .map((k, v) -> Tuple.of(nodeNameToId.get(k).get(), v.map(s -> nodeNameToId.get(s).get())));

        var dfsResult = new DfsRunner(neighbors, maxUppercaseId, maxAllId).dfs(0, List.empty());

//        System.out.println(dfsResult);
        System.out.println(dfsResult.size());
    }

    public static class DfsRunner {
        final Map<Integer, Set<Integer>> neighbors;

        final int maxUppercaseId;

        final int maxAllId;

        public DfsRunner(Map<Integer, Set<Integer>> neighbors, int maxUppercaseId, int maxAllId) {
            this.neighbors = neighbors;
            this.maxUppercaseId = maxUppercaseId;
            this.maxAllId = maxAllId;
        }

        private List<List<Integer>> dfs(int node, List<Integer> pathSoFar) {
            if (node == 0 && !pathSoFar.isEmpty()) return List.empty();
            if (node > maxUppercaseId &&
                    pathSoFar.contains(node) &&
                    containsDoubleVisit(pathSoFar)) return List.empty();

            var newPath = pathSoFar.prepend(node);

            if (node == 1) return List.of(newPath);

            return neighbors.get(node).get().toList().flatMap(next -> dfs(next, newPath));
        }

        private boolean containsDoubleVisit(List<Integer> list) {
            int[] arr = new int[maxAllId + 1];


            for(int x : list.filter(i -> i > maxUppercaseId)) {
                arr[x]++;
                if (arr[x] > 1) return true;
            }
            return false;
        }
    }
}
