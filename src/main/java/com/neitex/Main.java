package com.neitex;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class Main {
    private static class Vertex {
        public int x, y;

        Vertex(int consX, int consY) {
            x = consX;
            y = consY;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return x == vertex.x && y == vertex.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class QueueImpl<T> extends AbstractQueue<T> {
        private final LinkedList<T> elements;

        public QueueImpl() {
            elements = new LinkedList<>();
        }

        @Override
        public boolean isEmpty() {
            return elements.isEmpty();
        }

        @Override
        public Iterator<T> iterator() {
            return elements.iterator();
        }

        @Override
        public int size() {
            return elements.size();
        }

        @Override
        public boolean offer(T e) {
            elements.add(e);
            return true;
        }

        @Override
        public T poll() {
            Iterator<T> iter = elements.iterator();
            T t = iter.next();
            if (t != null) {
                iter.remove();
                return t;
            }
            return null;
        }

        @Override
        public T peek() {
            return elements.getFirst();
        }
    }

    private static final Vertex initialVertex = new Vertex(-1, -1); // out of vertexes array

    private static List<Vertex> getNextConnections(Vertex currentPoint, int graphSize) {
        List<Vertex> vertexes = new ArrayList<>(3);
        if (currentPoint.x + 1 < graphSize)
            vertexes.add(new Vertex(currentPoint.x + 1, currentPoint.y));
        if (currentPoint.y - 1 >= 0 && currentPoint.x + 1 < graphSize)
            vertexes.add(new Vertex(currentPoint.x + 1, currentPoint.y - 1));
        if (currentPoint.y + 1 < graphSize && currentPoint.x + 1 < graphSize)
            vertexes.add(new Vertex(currentPoint.x + 1, currentPoint.y + 1));
//        if (currentPoint.y - 1 > 0 && currentPoint.x - 1 > 0)
//            vertexes.add(new Vertex(currentPoint.x - 1, currentPoint.y - 1));
//        if (currentPoint.y + 1 < graphSize && currentPoint.x - 1 > 0)
//            vertexes.add(new Vertex(currentPoint.x - 1, currentPoint.y + 1));
        return vertexes;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int size = Integer.parseInt(reader.readLine());
        List<int[]> graphList = new ArrayList<>(size);
        MutableValueGraph<Vertex, Integer> graph = ValueGraphBuilder.directed().expectedNodeCount(size * size).build();
        for (int i = 0; i < size; i++) {
            String[] rowStr = reader.readLine().split(" ");
            int[] row = Arrays.stream(rowStr).mapToInt(Integer::parseInt).toArray();
            graphList.add(i, row);
            Vertex first = new Vertex(0, i);
            graph.putEdgeValue(initialVertex, first, row[0]);
//            graph.putEdgeValue(first, initialVertex, row[0]);
        }
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Vertex curr = new Vertex(x, y);
                List<Vertex> connected = getNextConnections(curr, size);
                for (Vertex successor : connected) {
                    graph.putEdgeValue(curr, successor, graphList.get(successor.y)[successor.x]);
                }
            }
        }
        HashMap<Vertex, Integer> resultCosts = new HashMap<>();
        HashMap<Vertex, Boolean> visited = new HashMap<>();
        QueueImpl<Vertex> queue = new QueueImpl<>();
//        resultCosts.put(initialVertex, 0);
//        queue.offer(initialVertex);
        for (Vertex vertex : graph.successors(initialVertex)) {
            queue.offer(vertex);
            resultCosts.put(vertex, graph.edgeValue(initialVertex, vertex).get()); // It is always present, as we got this vertex from successors
//            visited.put(vertex, true);
        }
        visited.put(initialVertex, true);
        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            assert currentVertex != null;
            int vertexCost = resultCosts.get(currentVertex);
            for (Vertex successor : graph.successors(currentVertex)) {
//                if (visited.containsKey(successor))
//                    continue;
                int cost = graph.edgeValue(currentVertex, successor).get() + vertexCost;
                if (resultCosts.getOrDefault(successor, Integer.MIN_VALUE) < cost)
                    resultCosts.put(successor, cost);
                if (!visited.containsKey(successor)) {
                    queue.offer(successor);
                }
            }
            visited.put(currentVertex, true);
        }
        System.out.println("all longest paths tree from initialVertex:");
        int longest = Integer.MIN_VALUE;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Vertex vertex = new Vertex(x, y);
                int result = resultCosts.get(vertex);
                System.out.println("initialVertex -> [" + vertex.y + "][" + vertex.x + "] = " + result);
                if (result > longest)
                    longest = result;
            }
        }
        System.out.println("longest path tree from vertex 'initialVertex' = "+longest);
    }
}
