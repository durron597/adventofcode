package org.jared.twentytwo;

import io.vavr.Lazy;
import io.vavr.collection.*;
import io.vavr.control.Option;
import lombok.*;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Volcano implements Runnable {
    public static void main(String... args) {
        new Volcano().run();
    }

    private static final Pattern p =
            Pattern.compile("^Valve ([A-Z][A-Z]) has flow rate=([0-9]+); tunnels? leads? to valves? ([A-Z, ]+)$");

    Map<String, Node> nodeMap = HashMap.empty();

    Map<SearchState, Integer> visitTime = HashMap.empty();

    @Override
    public void run() {
        Scanner sc = new Scanner(Volcano.class.getResourceAsStream("/2022/day16_input.txt"));

        ScanIterator iter = new ScanIterator(sc);

        nodeMap = iter.map(nextLine -> {
            Matcher m = p.matcher(nextLine);
            m.find();
            return new Node(m.group(1),
                    Integer.parseInt(m.group(2)),
                    Arrays.stream(m.group(3).split(", "))
                            .collect(List.collector()));
        }).toMap(Node::getId, Function.identity());

        LinkedList<SearchState> bfs = new LinkedList<>();
        Node startNode = nodeMap.get("AA").get();
        SearchState start = new SearchState(startNode,
                nodeMap.values()
                       .filter(n -> n.getFlowRate() == 0)
                       .toMap(Function.identity(), n -> 0));
        visitTime = visitTime.put(start, 4);
        bfs.add(start);

        while (!bfs.isEmpty()) {
            SearchState next = bfs.poll();
            final int newTime = visitTime.get(next).get() + 1;

            if (newTime == 31) break;

            if (next.getActivated().size() == nodeMap.size()) {
                bfs.add(next);
            } else {
                List<SearchState> nextStates = next.getCurrentNode()
                        .getNeighbors()
                        .map(node -> new SearchState(node, next.getActivated()))
                        .filter(state -> !visitTime.containsKey(state))
                        .prependAll(Option.of(next.getCurrentNode())
                                .filter(node -> !next.getActivated().containsKey(node))
                                .filter(node -> node.getFlowRate() > 0)
                                .map(node -> new SearchState(node, next.getActivated().put(node, newTime)))
                                .filter(state -> !visitTime.containsKey(state)));

                bfs.addAll(nextStates.toJavaList());
                visitTime = visitTime.merge(nextStates.toMap(Function.identity(), (x) -> newTime));
            }
        }

        bfs.stream().sorted(Comparator.comparingInt(t -> t.getScore()))
                .forEach(state -> System.out.format("Score: %d, state: %s%n", state.getScore(), state));
    }
    @Value
    public class Node {
        String id;
        int flowRate;
        List<String> neighborStrings;

        @Getter(AccessLevel.NONE)
        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        Lazy<List<Node>> neighbors = Lazy.of(() -> getNeighborStrings().flatMap(nodeMap::get));

        public List<Node> getNeighbors() {
            return neighbors.get();
        }
    }

    @Value
    public class SearchState {
        Node currentNode;

        Map<Node, Integer> activated;

        @Getter(AccessLevel.NONE)
        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        Lazy<Integer> score =
                Lazy.of(() -> getActivated().foldLeft(0, (x, n) -> x + (n._1().getFlowRate() * (30 - n._2()))));

        public int getScore() {
            return score.get();
        }
    }
}
