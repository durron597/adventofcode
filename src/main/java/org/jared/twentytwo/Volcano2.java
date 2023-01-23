package org.jared.twentytwo;

import io.vavr.Lazy;
import io.vavr.collection.HashMap;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.*;
import org.jared.util.ScanIterator;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalTime.*;

public class Volcano2 implements Runnable {
    public static void main(String... args) {
        new Volcano2().run();
    }

    private static final Pattern p =
            Pattern.compile("^Valve ([A-Z][A-Z]) has flow rate=([0-9]+); tunnels? leads? to valves? ([A-Z, ]+)$");

    Map<String, Node> nodeMap = HashMap.empty();

    Map<SearchState, Integer> visitTime = HashMap.empty();

    @Override
    public void run() {
        Scanner sc = new Scanner(Volcano2.class.getResourceAsStream("/2022/day16_input3.txt"));

        ScanIterator iter = new ScanIterator(sc);

        nodeMap = iter.map(nextLine -> {
            Matcher m = p.matcher(nextLine);
            m.find();
            return new Node(m.group(1),
                    Integer.parseInt(m.group(2)),
                    Arrays.stream(m.group(3).split(", "))   .collect(List.collector()));
        }).toMap(Node::getId, Function.identity());

        nodeMap.values()
                .map(node -> String.format("%s -> { %s };",
                        node.getId(),
                        node.getNeighborStrings().reduceLeft((x, y) -> x + " " + y)))
                .forEach(System.out::println);

        LinkedList<SearchState> bfs = new LinkedList<>();
        Node humanStartNode = nodeMap.get("OV").get();
        Node elephantStartNode = nodeMap.get("OV").get();

        SearchState preStart = new SearchState(nodeMap.get("JT").get(), nodeMap.get("JT").get(),
                nodeMap.values()
                        .filter(n -> n.getFlowRate() == 0)
                        .toMap(Function.identity(), n -> 0));
        SearchState start = new SearchState(humanStartNode, elephantStartNode,
                nodeMap.values()
                        .filter(n -> n.getFlowRate() == 0)
                        .toMap(Function.identity(), n -> 0));
        visitTime = visitTime.put(preStart, 5);
        visitTime = visitTime.put(start, 6);
        bfs.add(start);

        int max = 0;

        int maxScore = -1;

        while (!bfs.isEmpty()) {
            SearchState next = bfs.poll();
            final int newTime = visitTime.get(next).get() + 1;

            if (newTime > max) {
                System.out.println("Got to " + newTime + "!!! Time is: " + now());
                max = newTime;
            }

            if (newTime == 31) {
                break;
            }

            maxScore = Math.max(next.getScore(), maxScore);
            if (newTime <= 19 && newTime >= 13) {
                if ((next.getScore() + 50) * 2 < maxScore) continue;
            } else if (newTime >= 20) {
                if (next.getScore() + 100 < maxScore) continue;
            }

            if (next.getActivated().size() == nodeMap.size()) {
                bfs.add(next);
            } else {
                final List<Node> humanNeighbors = next.getCurrentNode().getNeighbors();
                final List<Node> elephantNeighbors = next.getElephantNode().getNeighbors();
                final Option<Node> turnOnHumanNode = Option.of(next.getCurrentNode())
                        .filter(node -> !next.getActivated().containsKey(node))
                        .filter(node -> node.getFlowRate() > 0);
                final Option<Node> turnOnElephantNode = Option.of(next.getElephantNode())
                        .filter(node -> !next.getActivated().containsKey(node))
                        .filter(node -> node.getFlowRate() > 0);

                Option<SearchState> twoTurnOn =
                        turnOnHumanNode.flatMap(human -> turnOnElephantNode.map(
                                        elephant -> new SearchState(human, elephant, next.getActivated()
                                                .put(human, newTime)
                                                .put(elephant, newTime))))
                                .filter(state -> !visitTime.containsKey(state));
                List<SearchState> elephantOn = humanNeighbors.flatMap(human -> turnOnElephantNode.map(
                                elephant -> new SearchState(human, elephant, next.getActivated().put(elephant, newTime))))
                        .filter(state -> !visitTime.containsKey(state));
                List<SearchState> humanOn = newTime == 7 ? List.empty() : elephantNeighbors.flatMap(elephant -> turnOnHumanNode.map(
                                human -> new SearchState(human, elephant, next.getActivated().put(human, newTime))))
                        .filter(state -> !visitTime.containsKey(state));
                Iterator<SearchState> bothMove = newTime == 7 ? Iterator.empty() : humanNeighbors
                        .crossProduct(elephantNeighbors)
                        .map(t -> new SearchState(t._1(), t._2(), next.getActivated()))
                        .filter(state -> !visitTime.containsKey(state));

                List<SearchState> nextStates = twoTurnOn.toList().appendAll(elephantOn).appendAll(humanOn).appendAll(bothMove);

                bfs.addAll(nextStates.toJavaList());

                visitTime = visitTime.merge(nextStates.toMap(Function.identity(), (x) -> newTime));
            }
        }

        System.out.println(bfs.size());

        final var lambdaMax = maxScore;

        bfs.stream().filter(state -> state.getScore() == lambdaMax)
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

        @Override
        public String toString() {
            return getId();
        }
    }

    @Value
    public class SearchState {
        Node currentNode;

        Node elephantNode;

        Map<Node, Integer> activated;

        @Getter(AccessLevel.NONE)
        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        Lazy<Integer> score =
                Lazy.of(() -> getActivated().foldLeft(0, (x, n) -> x + (n._1().getFlowRate() * (30 - n._2()))));

        public int getScore() {
            return score.get();
        }

        @Override
        public String toString() {
            return "SearchState{" +
                    "h=" + currentNode +
                    ", e=" + elephantNode +
                    ", act=" + activated.filterValues(i -> i > 0) +
                    '}';
        }
    }
}
