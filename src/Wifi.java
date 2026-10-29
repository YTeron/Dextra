import java.util.*;

public class Wifi {

    static class Edge {
        int to, w;
        Edge(int to, int w) { this.to = to; this.w = w; }
    }

    /**
     * Мульти-источниковая Дейкстра: старт сразу из всех точек входа.
     * @return сумма кратчайших расстояний до ближайшей точки входа.
     * Сложность O((r + e) log r).
     */
    public static long totalDelay(int r, List<int[]> edges, int[] entryPoints) {
        List<List<Edge>> graph = new ArrayList<>(r);
        for (int i = 0; i < r; i++) graph.add(new ArrayList<>());

        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            graph.get(u).add(new Edge(v, w));
            graph.get(v).add(new Edge(u, w));
        }

        final long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[r];
        Arrays.fill(dist, INF);

        // Приоритетная очередь пар (dist, vertex)
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));

        for (int s : entryPoints) {
            if (dist[s] != 0) {
                dist[s] = 0;
                pq.add(new long[]{0, s});
            }
        }

        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            long d = cur[0];
            int u = (int) cur[1];
            if (d > dist[u]) continue;              // устаревшая запись

            for (Edge e : graph.get(u)) {
                long nd = dist[u] + e.w;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    pq.add(new long[]{nd, e.to});
                }
            }
        }

        long total = 0;
        for (long d : dist) total += d;
        return total;
    }
}
