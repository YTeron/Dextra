import java.util.*;

public class DextraFly {
    static class Edge {
        int to, w;
        Edge(int to, int w) { this.to = to; this.w = w; }
    }

    static int c;
    static List<List<Edge>> adj;

    static int[] dijkstra(int s, int[] parent) {
        if (s < 0 || s >= c)
            throw new IllegalArgumentException("Старт вне диапазона: " + s);

        final int INF = Integer.MAX_VALUE / 4;
        int[] dist = new int[c];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[s] = 0;

        PriorityQueue<int[]> pq =
                new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.add(new int[]{0, s});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], u = cur[1];
            if (d > dist[u]) continue;

            for (Edge e : adj.get(u)) {
                if (e.to < 0 || e.to >= c)
                    throw new IllegalStateException("Ребро в несуществующий город: " + e.to);
                int nd = dist[u] + e.w;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    parent[e.to] = u;
                    pq.add(new int[]{nd, e.to});
                }
            }
        }

        for (int i = 0; i < c; i++) if (dist[i] == INF) dist[i] = -1;
        return dist;
    }

    static List<Integer> reconstruct(int u, int v, int[] parent, int[] dist) {
        if (dist[v] == -1) return null;
        LinkedList<Integer> path = new LinkedList<>();
        for (int x = v; x != -1; x = parent[x]) {
            path.addFirst(x);
            if (x == u) break;
        }
        return path;
    }

    public static int solve(int cityCount, List<int[]> flights,
                            int home, int[] tourists, int finish,
                            List<Integer> bestRoute) {

        if (tourists == null || tourists.length != 3)
            throw new IllegalArgumentException("Нужно ровно 3 туристических города");
        c = cityCount;

        if (home < 0 || home >= c) throw new IllegalArgumentException("home: " + home);
        if (finish < 0 || finish >= c) throw new IllegalArgumentException("finish: " + finish);
        for (int t : tourists)
            if (t < 0 || t >= c) throw new IllegalArgumentException("tourist: " + t);
        for (int[] f : flights)
            if (f == null || f.length < 2 ||
                    f[0] < 0 || f[0] >= c || f[1] < 0 || f[1] >= c)
                throw new IllegalArgumentException("Рейс вне диапазона: " + Arrays.toString(f));

        adj = new ArrayList<>(c);
        for (int i = 0; i < c; i++) adj.add(new ArrayList<>());
        for (int[] f : flights) adj.get(f[0]).add(new Edge(f[1], 1));

        LinkedHashSet<Integer> specialSet = new LinkedHashSet<>();
        specialSet.add(home);
        for (int t : tourists) specialSet.add(t);
        specialSet.add(finish);
        List<Integer> special = new ArrayList<>(specialSet);
        int m = special.size();
        Map<Integer, Integer> idx = new HashMap<>();
        for (int i = 0; i < m; i++) idx.put(special.get(i), i);

        int[][] distFrom = new int[m][];
        int[][] parentFrom = new int[m][];
        for (int i = 0; i < m; i++) {
            parentFrom[i] = new int[c];
            distFrom[i] = dijkstra(special.get(i), parentFrom[i]);
        }

        int[][] perms = {
                {0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}
        };
        int best = Integer.MAX_VALUE;
        int[] bestPerm = null;

        for (int[] p : perms) {
            int p1 = tourists[p[0]], p2 = tourists[p[1]], p3 = tourists[p[2]];
            int d1 = distFrom[idx.get(home)][p1];
            int d2 = distFrom[idx.get(p1)][p2];
            int d3 = distFrom[idx.get(p2)][p3];
            int d4 = distFrom[idx.get(p3)][finish];
            if (d1 < 0 || d2 < 0 || d3 < 0 || d4 < 0) continue;
            int total = d1 + d2 + d3 + d4;
            if (total < best) {
                best = total;
                bestPerm = new int[]{home, p1, p2, p3, finish};
            }
        }

        if (bestPerm == null) {
            bestRoute.clear();
            return -1;
        }

        bestRoute.clear();
        for (int i = 0; i < 4; i++) {
            int u = bestPerm[i], v = bestPerm[i + 1];
            int si = idx.get(u);
            List<Integer> seg = reconstruct(u, v, parentFrom[si], distFrom[si]);
            if (seg == null) {
                bestRoute.clear();
                return -1;
            }
            if (i > 0 && !bestRoute.isEmpty()) seg.remove(0);
            bestRoute.addAll(seg);
        }
        return best;
    }
}
