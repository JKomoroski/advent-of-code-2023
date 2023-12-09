import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day9 extends AOCBase {

    public static void main(String[] args) throws Exception {
        new Day9().run();
    }


    protected Day9() {
        super("day-9", "input.txt");
    }

    @Override
    void part1(Stream<String> fileInput) throws Exception {
        final var result = fileInput.map(Day9::fromString)
                .map(Day9::wrapHistory)
                .map(Day9::calculateDiffTree)
                .map(Day9::extrapolateHistoryEnd)
                .mapToLong(List::getLast)
                .sum();
        System.out.println("Part 1: " + result); //1708206096
    }

    @Override
    void part2(Stream<String> fileInput) throws Exception {
        final var sum = fileInput.map(Day9::fromString)
                .map(Day9::wrapHistory)
                .map(Day9::calculateDiffTree)
                .map(Day9::extrapolateHistoryStart)
                .mapToLong(List::getLast)
                .sum();



        System.out.println("Part 2: " + sum);

    }

    static List<History> wrapHistory(History history) {
        final List<History> tree = new ArrayList<>();
        tree.add(history);
        return tree;
    }

    static History fromString(String s) {
        return new History(Arrays.stream(s.split(" ")).mapToLong(Long::parseLong).boxed().collect(Collectors.toList()));
    }

    record History(List<Long> longs) {}
    static List<Long> extrapolateHistoryStart(List<History> tree) {
        final var collect = tree.stream()
                .map(h -> h.longs.getFirst())
                .collect(Collectors.toList());

        final var extrapolations = new ArrayList<Long>();
        extrapolations.add(0L);

        for (int i = collect.size() - 2; i >= 0; i--) {
            final var abs = collect.get(i) - extrapolations.getLast();
            extrapolations.add(abs);
        }

        return extrapolations;
    }

    static List<Long> extrapolateHistoryEnd(List<History> tree) {
        final var collect = tree.stream()
                .map(h -> h.longs.getLast())
                .collect(Collectors.toList());

        final var extrapolations = new ArrayList<Long>();
        extrapolations.add(0L);

        for (int i = collect.size() - 1; i >= 0; i--) {
            final var abs = collect.get(i) + extrapolations.getLast();
            extrapolations.add(abs);
        }

        return extrapolations;
    }

    static List<History> calculateDiffTree(List<History> tree) {
        final var last = tree.get(tree.size() - 1);

        if(last.longs().stream().allMatch(l -> l == 0L)) {
            return tree;
        }

        final var newLongs = IntStream.range(1, last.longs.size())
                .mapToLong(i -> last.longs.get(i) - last.longs.get(i - 1))
                .boxed()
                .collect(Collectors.toList());
        tree.add(new History(newLongs));
        return calculateDiffTree(tree);
    }
}
