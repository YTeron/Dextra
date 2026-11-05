package allAlgebra;

import java.util.*;

public class Decstra1 {
    static final long INF = Long.MAX_VALUE / 4;

    static class Edge {
        int to;
        long w;
        Edge(int to, long w) { this.to = to; this.w = w; }
    }

    static class Pair implements Comparable<Pair> {
        long d;
        int v;
        Pair(long d, int v) { this.d = d; this.v = v; }
        public int compareTo(Pair o) { return Long.compare(this.d, o.d); }
    }

    static long[] dijkstra(int n, List<List<Edge>> adj, int start) {
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[start] = 0;
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(0, start));
        while (!pq.isEmpty()) {
            Pair cur = pq.poll();
            if (cur.d > dist[cur.v]) continue;
            for (Edge e : adj.get(cur.v)) {
                long nd = cur.d + e.w;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    pq.add(new Pair(nd, e.to));
                }
            }
        }
        return dist;
    }
    static long[] dijkstraWithParent(int n, List<List<Edge>> adj, int start, int[] parent) {
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[start] = 0;
        parent[start] = -1;
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(0, start));
        while (!pq.isEmpty()) {
            Pair cur = pq.poll();
            if (cur.d > dist[cur.v]) continue;
            for (Edge e : adj.get(cur.v)) {
                long nd = cur.d + e.w;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    parent[e.to] = cur.v;
                    pq.add(new Pair(nd, e.to));
                }
            }
        }
        return dist;
    }

    static List<Integer> buildPath(int[] parent, int from, int to) {
        List<Integer> path = new ArrayList<>();
        if (from == to) { path.add(from); return path; }
        int cur = to;
        while (cur != -1) {
            path.add(cur);
            if (cur == from) break;
            cur = parent[cur];
        }
        if (path.get(path.size() - 1) != from) return null;
        Collections.reverse(path);
        return path;
    }

    static long[] multiSourceDijkstra(int n, List<List<Edge>> adj, List<Integer> sources) {
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for (int s : sources) {
            dist[s] = 0;
            pq.add(new Pair(0, s));
        }
        while (!pq.isEmpty()) {
            Pair cur = pq.poll();
            if (cur.d > dist[cur.v]) continue;
            for (Edge e : adj.get(cur.v)) {
                long nd = cur.d + e.w;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    pq.add(new Pair(nd, e.to));
                }
            }
        }
        return dist;
    }

    static void solveDelay(int n, long[] distS) {
        long sum = 0, max = 0;
        List<Integer> farthest = new ArrayList<>();
        List<Integer> unreachable = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (distS[i] == INF) { unreachable.add(i); continue; }
            sum += distS[i];
            if (distS[i] > max) {
                max = distS[i];
                farthest.clear();
                farthest.add(i);
            } else if (distS[i] == max) {
                farthest.add(i);
            }
        }

        int reachable = n - unreachable.size();
        double avg = reachable > 0 ? (double) sum / reachable : 0;

        System.out.println("Задача 1: Задержки до ближайшей точки входа");
        for (int i = 0; i < n; i++) {
            System.out.printf("  Маршрутизатор %d: %s%n",
                    i, distS[i] == INF ? "недостижим" : distS[i] + " м");
        }
        System.out.println("Общая задержка (сумма расстояний): " + sum);
        System.out.printf("Средняя задержка: %.2f м%n", avg);

        System.out.println("\nЗадача 2: Самые удалённые маршрутизаторы");
        System.out.println("Максимальная задержка: " + max);
        System.out.println("Маршрутизаторы с максимальной задержкой: " + farthest);
        if (!unreachable.isEmpty())
            System.out.println("Недостижимые маршрутизаторы: " + unreachable);
    }

    static void solveRoute(int n, List<List<Edge>> adj, int start, int[] targets) {
        int[] nodes = {start, targets[0], targets[1], targets[2]};
        int k = nodes.length;
        long[][] d = new long[k][k];
        int[][] parents = new int[k][n];
        for (int i = 0; i < k; i++) {
            d[i] = dijkstraWithParent(n, adj, nodes[i], parents[i]);
        }

        int[] perm = {1, 2, 3};
        long best = INF;
        int[] bestOrder = null;
        do {
            long total = 0;
            int prev = 0;
            boolean ok = true;
            for (int idx : perm) {
                if (d[prev][idx] == INF) { ok = false; break; }
                total += d[prev][idx];
                prev = idx;
            }
            if (ok && d[prev][0] != INF) {
                total += d[prev][0];
                if (total < best) {
                    best = total;
                    bestOrder = perm.clone();
                }
            }
        } while (nextPermutation(perm));

        System.out.println("\nЗадача 2: Маршрут специалиста");
        System.out.println("Старт: " + start + ", цели: " + Arrays.toString(targets));
        if (bestOrder == null) {
            System.out.println("Маршрут невозможен.");
            return;
        }
        StringBuilder orderSb = new StringBuilder("Порядок: " + start);
        for (int idx : bestOrder) orderSb.append(" -> ").append(nodes[idx]);
        orderSb.append(" -> ").append(start);
        System.out.println(orderSb);

        List<Integer> fullPath = new ArrayList<>();
        int prev = 0;
        fullPath.add(nodes[0]);
        for (int idx : bestOrder) {
            List<Integer> seg = buildPath(parents[prev], nodes[prev], nodes[idx]);
            for (int j = 1; j < seg.size(); j++) fullPath.add(seg.get(j));
            prev = idx;
        }
        List<Integer> back = buildPath(parents[prev], nodes[prev], nodes[0]);
        for (int j = 1; j < back.size(); j++) fullPath.add(back.get(j));

        System.out.println("Полный маршрут: " + fullPath);
        System.out.println("Суммарная длина (с возвратом): " + best + " м");
    }

    static boolean nextPermutation(int[] a) {
        int i = a.length - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;
        if (i < 0) return false;
        int j = a.length - 1;
        while (a[j] <= a[i]) j--;
        int t = a[i]; a[i] = a[j]; a[j] = t;
        for (int l = i + 1, r = a.length - 1; l < r; l++, r--) {
            t = a[l]; a[l] = a[r]; a[r] = t;
        }
        return true;
    }

    static int solveBestEntry(int n, List<List<Edge>> adj,
                              long[] distS, int[] candidates) {
        System.out.println("\nЗадача 4: Выбор новой точки входа");
        int bestV = -1;
        long bestWorst = INF;

        for (int v : candidates) {
            long[] dv = dijkstra(n, adj, v);
            long worst = 0;
            for (int u = 0; u < n; u++) {
                long m = Math.min(distS[u], dv[u]);
                if (m == INF) { worst = INF; break; }
                if (m > worst) worst = m;
            }
            System.out.printf("Кандидат %d: максимальная задержка после добавления = %s%n",
                    v, worst == INF ? "недостижимы" : worst + " м");
            if (worst < bestWorst) {
                bestWorst = worst;
                bestV = v;
            }
        }

        System.out.println("Рекомендуемая точка входа: маршрутизатор " + bestV);
        System.out.println("Максимальная задержка после её установки: " + bestWorst + " м");
        return bestV;
    }

    static class Input {
        int n;
        List<List<Edge>> adj;
        List<Integer> sources;
        int start;
        int[] targets = new int[3];
        int[] candidates = new int[3];
    }

    static Input readInput(Scanner sc) {
        Input in = new Input();

        System.out.print("Введите число маршрутизаторов r: ");
        in.n = sc.nextInt();

        in.adj = new ArrayList<>();
        for (int i = 0; i < in.n; i++) in.adj.add(new ArrayList<>());

        System.out.print("Введите число кабелей m: ");
        int m = sc.nextInt();

        System.out.println("Введите " + m + " строк: u v w (вершины 0.." + (in.n - 1) + ", вес > 0):");
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            long w = sc.nextLong();
            in.adj.get(u).add(new Edge(v, w));
            in.adj.get(v).add(new Edge(u, w));
        }

        System.out.print("Введите количество точек входа k: ");
        int k = sc.nextInt();
        in.sources = new ArrayList<>();
        System.out.print("Введите " + k + " номеров точек входа: ");
        for (int i = 0; i < k; i++) in.sources.add(sc.nextInt());

        System.out.print("Введите стартовый маршрутизатор специалиста: ");
        in.start = sc.nextInt();

        System.out.print("Введите три маршрутизатора для посещения: ");
        for (int i = 0; i < 3; i++) in.targets[i] = sc.nextInt();

        System.out.print("Введите три возможных места для новой точки входа: ");
        for (int i = 0; i < 3; i++) in.candidates[i] = sc.nextInt();

        return in;
    }

    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Input in = readInput(sc);

        long[] distS = multiSourceDijkstra(in.n, in.adj, in.sources);
        solveDelay(in.n, distS);
        solveRoute(in.n, in.adj, in.start, in.targets);
        solveBestEntry(in.n, in.adj, distS, in.candidates);
    }
}
