package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.jared.util.ScanIterator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class SurfaceArea {

    public static final List<Tuple3<Integer, Integer, Integer>> NEIGHBORS = List.of(
        Tuple.of(-1, 0, 0),
        Tuple.of(1, 0, 0),
        Tuple.of(0, -1, 0),
        Tuple.of(0, 1, 0),
        Tuple.of(0, 0, -1),
        Tuple.of(0, 0, 1)
    );

    public static void main(String... args) throws Exception {
        Scanner sc = new Scanner(SurfaceArea.class.getResourceAsStream("/2022/day18_input.txt"));

        ScanIterator iter = new ScanIterator(sc);

        final Set<Tuple3<Integer, Integer, Integer>> inputData = iter.map(str -> str.split(","))
                .map(arr -> Tuple.of(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2])))
                .toSet();

        final Tuple3<Integer, Integer, Integer> maximums = inputData.fold(Tuple.of(-1, -1, -1),
                (t1, t2) -> Tuple.of(Math.max(t1._1(), t2._1()), Math.max(t1._2(), t2._2()), Math.max(t1._3(), t2._3())));

        Tuple2<Set<Tuple3<Integer, Integer, Integer>>, Set<Tuple3<Integer, Integer, Integer>>> pocketDetection =
                List.range(0, maximums._1())
                .crossProduct(List.range(0, maximums._2())).toList()
                .crossProduct(List.range(0, maximums._3()))
                .map(t -> t._1().concat(Tuple.of(t._2())))
                .foldLeft(Tuple.of(HashSet.empty(),
                                 HashSet.empty()),
                        (visited, next) -> dfs(visited, next, inputData, maximums));

        for(int z = 0; z < maximums._3(); z++) {
            for(int y = 0; y < maximums._2(); y++) {
                for(int x = 0; x < maximums._1(); x++) {
                    if (inputData.contains(Tuple.of(x, y, z))) {
                        System.out.print("#");
                    } else if(pocketDetection._1().contains(Tuple.of(x, y, z))) {
                        System.out.print("*");
                    } else {
                        System.out.print(".");
                    }
                }
                System.out.println();
            }
            System.out.println("\n----" + z + "----\n");
        }

        Tuple3<HashSet<Tuple3<Integer, Integer, Integer>>, Integer, Integer> result = inputData
                .addAll(pocketDetection._1())
                .foldLeft(Tuple.of(HashSet.empty(), 0, 0), ((out, t) ->
                        Tuple.of(out._1().add(t), out._2() + 6,
                                NEIGHBORS.map(delta -> delta.map1(i -> t._1() + i).map2(i -> t._2() + i).map3(i -> t._3() + i))
                                        .filter(out._1()::contains)
                                        .foldLeft(out._3(), (sum, neighbor) -> sum + 1))));

        System.out.println(result._2() - 2 * result._3());
    }

    private static Tuple2<Set<Tuple3<Integer, Integer, Integer>>, Set<Tuple3<Integer, Integer, Integer>>>
        dfs(Tuple2<Set<Tuple3<Integer, Integer, Integer>>, Set<Tuple3<Integer, Integer, Integer>>> visited,
            Tuple3<Integer, Integer, Integer> next,
            Set<Tuple3<Integer, Integer, Integer>> realCubes,
            Tuple3<Integer, Integer, Integer> maximums) {
        if (visited._1().contains(next) || visited._2().contains(next) || realCubes.contains(next)) return visited;
        return dfsHelper(visited._1(), visited._2(), realCubes, HashSet.of(next), next, maximums);
    }

    private static Tuple2<Set<Tuple3<Integer, Integer, Integer>>, Set<Tuple3<Integer, Integer, Integer>>>
        dfsHelper(Set<Tuple3<Integer, Integer, Integer>> in,
                       Set<Tuple3<Integer, Integer, Integer>> out,
                       Set<Tuple3<Integer, Integer, Integer>> realCubes,
                       Set<Tuple3<Integer, Integer, Integer>> visitedSoFar,
                       Tuple3<Integer, Integer, Integer> next,
                       Tuple3<Integer, Integer, Integer> maximums) {
        final Set<Tuple3<Integer, Integer, Integer>> result = dfsInnerHelper(in, out, realCubes, visitedSoFar, next, maximums);

        var outOfBounds = result
                .find(e -> e._1() > maximums._1() || e._1() < 0 ||
                        e._2() > maximums._2() || e._2() < 0 ||
                        e._3() > maximums._3() || e._3() < 0);

        if (outOfBounds.isEmpty()) {
            return Tuple.of(in.addAll(result), out);
        } else {
            return Tuple.of(in, out.addAll(result));
        }
    }

    private static Set<Tuple3<Integer, Integer, Integer>>
        dfsInnerHelper(Set<Tuple3<Integer, Integer, Integer>> in,
                  Set<Tuple3<Integer, Integer, Integer>> out,
                  Set<Tuple3<Integer, Integer, Integer>> realCubes,
                  Set<Tuple3<Integer, Integer, Integer>> visitedSoFar,
                  Tuple3<Integer, Integer, Integer> next,
                  Tuple3<Integer, Integer, Integer> maximums) {
        Queue<Tuple3<Integer, Integer, Integer>> bfs = new LinkedList<>();
        bfs.add(next);

        final var visited = visitedSoFar.toJavaSet();

        while(!bfs.isEmpty()) {
            final var poll = bfs.poll();

            var remaining =
                    NEIGHBORS.map(delta -> delta.map1(i -> poll._1() + i).map2(i -> poll._2() + i).map3(i -> poll._3() + i))
                    .filter(e -> !in.contains(e))
                    .filter(e -> !out.contains(e))
                    .filter(e -> !visited.contains(e))
                    .filter(e -> !realCubes.contains(e))
                    .filter(e -> e._1() <= maximums._1() + 1 && e._1() >= -1
                            && e._2() <= maximums._2() + 1 && e._2() >= -1
                            && e._3() <= maximums._3() + 1 && e._3() >= -1);

            visited.addAll(remaining.toJavaList());

            bfs.addAll(remaining.toJavaSet());
        }

        return HashSet.ofAll(visited);
    }
}
