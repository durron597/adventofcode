package org.jared.twentytwo;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jared.util.Dir;

import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.function.Function;

public class Climb {
    public static void main(String... args) throws Throwable {
        Scanner sc = new Scanner(Climb.class.getResourceAsStream("/2022/day12_input.txt"));

        List<String> lines = List.empty();

        while(sc.hasNextLine()) {
            String next = sc.nextLine();
            lines = lines.append(next);
        }

        char[][] elevation = new char[lines.size()][];

        for(int i = 0; i < lines.size(); i++) {
            elevation[i] = lines.get(i).toCharArray();
        }

        Node[][] nodes = new Node[elevation.length][elevation[0].length];

        Node start = null;
        Node end = null;

        for (int x = 0; x < elevation[0].length; x++) {
            for (int y = 0; y < elevation.length; y++) {
                char value = elevation[y][x];
                char height = value;
                if (value == 'S') height = 'a';
                if (value == 'E') height = 'z';
                nodes[y][x] = new Node(nodes, x, y, height);
                if (value == 'S') {
                    start = nodes[y][x];
                    start.g = 0;
                }
                if (value == 'E') end = nodes[y][x];
            }
        }

        Node result = aStar(start, end);

        printPath(nodes, end);
    }

    public static void printPath(Node[][] grid, Node target){
        Node n = target;

        if(n==null)
            return;

        List<Node> nodes = List.empty();

        while(n.parent != null){
            nodes = nodes.prepend(n);
            n = n.parent;
        }
        nodes = nodes.prepend(n);

        System.out.println(nodes.size());

        Map<Integer, Tuple2<Node, Integer>> pathMap =
                nodes.zipWithIndex().toMap(t -> t._1().getX() * 128 + t._1().getY(), Function.identity());

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                int key = x * 128 + y;
                char res = pathMap.get(key).map(v -> (char) ((v._1().getHeight()))).getOrElse('.');
                System.out.print(res);
            }
            System.out.println();
        }
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Node implements Comparable<Node> {
        public int id;

        // Parent in the path
        public Node parent = null;

        private List<Node> neighbors = null;

        @Getter
        @EqualsAndHashCode.Include
        int x;

        @Getter
        @EqualsAndHashCode.Include
        int y;
        @Getter
        char height;

        @Getter
        Node[][] grid;

        // Evaluation functions
        public double f = Double.MAX_VALUE;
        public double g = Double.MAX_VALUE;

        Node(Node[][] grid, int x, int y, char height){
            this.grid = grid;
            this.x = x;
            this.y = y;
            this.height = height;
        }

        @Override
        public int compareTo(Node n) {
            return Double.compare(this.f, n.f);
        }

        public List<Node> getNeighbors() {
            if (neighbors == null) {
                neighbors = List.of(Dir.values())
                        .flatMap(d -> x + d.getX() >= 0 && x + d.getX() < getGrid()[0].length &&
                                y + d.getY() >= 0 && y + d.getY() < getGrid().length &&
                                getGrid()[y + d.getY()][x + d.getX()].getHeight() - this.getHeight() < 2 ?
                                Option.some(getGrid()[y + d.getY()][x + d.getX()]) :
                                Option.none());
            }
            return neighbors;
        }

        public double calculateHeuristic(Node target){
            return /* Math.abs(this.getX() - target.getX()) +
                    Math.abs(this.getY() - target.getY()) + */
                    Math.max(target.getHeight() - this.getHeight(), 0) * 10;
        }
    }

    public static Node aStar(Node start, Node target){
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        start.f = start.g + start.calculateHeuristic(target);
        openList.add(start);

        while(!openList.isEmpty()){
            Node n = openList.peek();
            if(n == target){
                return n;
            }

            for(Node m : n.getNeighbors()){
                double totalWeight = n.g + 1;

                if(!openList.contains(m) && !closedList.contains(m)){
                    m.parent = n;
                    m.g = totalWeight;
                    m.f = m.g + m.calculateHeuristic(target);
                    openList.add(m);
                } else {
                    if(totalWeight < m.g){
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = m.g + m.calculateHeuristic(target);

                        if(closedList.contains(m)){
                            closedList.remove(m);
                            openList.add(m);
                        }
                    }
                }
            }

            openList.remove(n);
            closedList.add(n);
        }
        return null;
    }
}