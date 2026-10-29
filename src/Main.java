//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    int r = 5;
    List<int[]> edges = new ArrayList<>();
    edges.add(new int[]{0, 1, 2});
    edges.add(new int[]{1, 2, 3});
    edges.add(new int[]{2, 3, 1});
    edges.add(new int[]{3, 4, 4});
    int[] entry = {0, 4};

    System.out.println(Wifi.totalDelay(r, edges, entry));

    int c = 6;
    int home = 0;
    int[] tourists = {1, 2, 3};
    int finish = 5;

    List<int[]> flights = new ArrayList<>();
    flights.add(new int[]{0, 1});
    flights.add(new int[]{1, 2});
    flights.add(new int[]{2, 3});
    flights.add(new int[]{3, 5});
    flights.add(new int[]{0, 2});
    flights.add(new int[]{2, 1});
    flights.add(new int[]{1, 3});
    flights.add(new int[]{3, 1});
    flights.add(new int[]{5, 0});

    List<Integer> route = new ArrayList<>();
    int total = DextraFly.solve(c, flights, home, tourists, finish, route);

    if (total < 0) {
        System.out.println("Маршрут невозможен");
    } else {
        System.out.println("Маршрут: " + route);
    }
}

