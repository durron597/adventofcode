package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.*;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Volcano3 {
    @Value
    @Builder(toBuilder = true)
    @AllArgsConstructor
    public static class State {
        int time;
        int currentValve;
        int currentFlow;
        long toOpen;

        long original;
    }

    static final int MINUTES = 26;
    static final int MAX = 10000;

    static int nonZeroCount;
    static Map<Integer, Tuple2<Integer, List<Integer>>> indexGraph;

    static Map<Integer, Node> indexToNode;
    static int[][] allPairs;

    private static final Pattern p =
            Pattern.compile("^Valve ([A-Z][A-Z]) has flow rate=([0-9]+); tunnels? leads? to valves? ([A-Z, ]+)$");


    public static void main(String... args) {

        //parse graph and assign indexes to each valve
        //making sure all the valves that can be opened are assigned an index first
        Scanner sc = new Scanner(Volcano.class.getResourceAsStream("/2022/day16_input.txt"));

        ScanIterator iter = new ScanIterator(sc);

        Map<String, Node> nodeMap = iter.map(nextLine -> {
            Matcher m = p.matcher(nextLine);
            m.find();
            return new Node(m.group(1),
                    Integer.parseInt(m.group(2)),
                    Arrays.stream(m.group(3).split(", "))
                            .collect(List.collector()));
        }).toMap(Node::getId, Function.identity());

        nonZeroCount = nodeMap.values().filter(n -> n.getFlowRate() > 0).size();
        var nodeToIndex = nodeMap.values().sorted(Comparator.comparingInt(Node::getFlowRate).reversed()).zipWithIndex().toMap(Function.identity());
//            stringGraph.OrderByDescending(kvp => kvp.Value.Item1).Select((kvp, i) => (kvp.Key, i)).ToDictionary(tp => tp.Key, tp => tp.i);
        indexToNode = nodeToIndex.toMap(Tuple2::_2, Tuple2::_1);
        indexGraph = nodeToIndex.map((node, index) -> Tuple.of(index, Tuple.of(node.getFlowRate(), node.getNeighborStrings().flatMap(nodeMap::get).flatMap(nodeToIndex::get))));

        //get all pairs shortest paths using Floyd-Warshall
        //note this is the DISTANCE only so still need to take into account time to open the valve
        allPairs = floydWarshall(indexGraph);

        //partition the non-zero valves into all possible disjoint sets
        //skipping 1 bit to ensure that we don't count both (A,B) and (B,A)
        List<Tuple2<Long, Long>> sets = List.empty();
        for (long i = 0; i < Math.pow(2, nonZeroCount - 1); i++) {
            Tuple2<Long, Long> nextSet = Tuple.of(0L, 0L);
            for (int b = 0; b < nonZeroCount; b++) {
                final var lambdaB = b;
                if (IsBitSet(b, i))
                    nextSet = nextSet.map1(l -> SetBit(lambdaB, l));
                else
                    nextSet = nextSet.map2(l -> SetBit(lambdaB, l));
            }
            sets = sets.prepend(nextSet);
        }

        //for each pair of disjoint sets, recursively DFS each side starting at valve AA
        //at each level of the search, we only need to consider moving to each valve which remains open
        //Find the max of the sum of the pressure released for all disjoint pairs
        var maximums = sets
                .toMap(Function.identity(), t -> t.map1(l -> Solve(l, nodeMap.get("AA").flatMap(nodeToIndex::get).get()))
                        .map2(l -> Solve(l, nodeMap.get("AA").flatMap(nodeToIndex::get).get())));


        maximums.maxBy(entry -> entry._2()._1() + entry._2()._2()).peek(t -> {
            var human = List.range(0, nonZeroCount).filter(b -> IsBitSet(b, t._1()._1())).flatMap(indexToNode::get);
            var elephant = List.range(0, nonZeroCount).filter(b -> IsBitSet(b, t._1()._2())).flatMap(indexToNode::get);
            System.out.println("Human disjoint is: " + t._1()._1());
            System.out.println("Human went to: " + human);
            System.out.println("For a value of " + t._2()._1());
            System.out.println("Elephant went to: " + elephant);
            System.out.println("For a value of " + t._2()._2());
        });
    }

    static boolean IsBitSet(int bit, long state) {
        return (state & (1L << bit)) != 0;
    }

    static long SetBit(int bit, long state) {
        return state |= 1L << bit;
    }

    static long ClearBit(int bit, long state) {
        return state &= ~(1L << bit);
    }

    static int Solve(long toOpen, int startValve) {
        return new Task(new State(0, startValve, 0, toOpen, toOpen), 0, true).call();
    }

    public static class Task implements Callable<Integer> {
        public static Map<State, Integer> cache = HashMap.empty();
        int currentBest = 0;

        boolean isTopLevel;

        State state;

        public Task(State state, int currentBest, boolean isTopLevel) {
            this.state = state;
            this.currentBest = currentBest;
            this.isTopLevel = isTopLevel;
        }

        public Task(State state, int currentBest) {
            this(state, currentBest, false);
        }

        @Override
        public Integer call() {
            if (cache.containsKey(state)) {
                return cache.get(state).get();
            }

            //calculate upper limit of pressure released assuming all remaining valves are opened in min time for any valve
            //also remove any valves from our set for where it isn't possible to open them
            //and form a priority queue based on the total pressure released by moving to that valve next
            var queue = new PriorityQueue<Tuple2<Integer, Integer>>(Comparator.comparingInt(Tuple2::_2));
            int maxFlow = 0;
            for (int b = 0; b < nonZeroCount; b++)
                if (IsBitSet(b, state.toOpen))
                    if (allPairs[state.currentValve][b] + state.time + 1 < MINUTES) {
                        var additionalFlow = (MINUTES - (allPairs[state.currentValve][b] + state.time + 1)) * (indexGraph.get(b).get()._1());
                        maxFlow += additionalFlow;
                        queue.offer(Tuple.of(b, -additionalFlow));
                    } else {
                        state = state.toBuilder().toOpen(ClearBit(b, state.getToOpen())).build();
                    }

            int best = state.getCurrentFlow();
            //if we can beat the current global best for these sets then keep trying
            if (state.currentFlow + maxFlow > currentBest) {
                while (!queue.isEmpty()) {
                    var next = queue.poll();
                    int nextValve = next._1();
                    int negAdditionalFlow = next._2();

                    //next state is the minute after we have opened this valve
                    State nextState = state.toBuilder()
                            .time(allPairs[state.currentValve][nextValve] + state.time + 1)
                            .currentFlow(state.currentFlow - negAdditionalFlow)
                            .currentValve(nextValve)
                            .toOpen(ClearBit(nextValve, state.toOpen))
                            .build();
                    int nextBest = new Task(
                            nextState, currentBest).call();

                    best = Math.max(best, nextBest);
                    if (state.getOriginal() == 14320 && best > currentBest) {
                        List.range(0, nonZeroCount).filter(b -> IsBitSet(b, state.getToOpen())).flatMap(indexToNode::get);
                        System.out.print("To Open: " +                         List.range(0, nonZeroCount).filter(b -> IsBitSet(b, state.getToOpen())).flatMap(indexToNode::get));
                        System.out.println(" Running with nextValve: " + indexToNode.get(nextValve));
                    }
                    currentBest = Math.max(best, currentBest);
                }
            }

            cache = cache.put(state, best);
            return best;
        }
    }


    private static int[][] floydWarshall(Map<Integer, Tuple2<Integer, List<Integer>>> graph) {
        int[][] distance = new int[graph.length()][graph.length()];
        for (int i = 0; i < graph.length(); i++)
            for (int j = 0; j < graph.length(); j++)
                distance[i][j] = MAX;

        for (var entry : graph)
            for (var edge : entry._2()._2())
                distance[entry._1()][edge] = 1;

        for (int k = 0; k < graph.length(); k++)
            for (int i = 0; i < graph.length(); i++)
                for (int j = 0; j < graph.length(); j++) {
                    if (distance[i][k] + distance[k][j] < distance[i][j]) {
                        distance[i][j] = distance[i][k] + distance[k][j];
                    }
                }

        return distance;
    }

    @Value
    public static class Node {
        String id;
        int flowRate;
        List<String> neighborStrings;

        @Override
        public String toString() {
            return getId();
        }
    }
}
