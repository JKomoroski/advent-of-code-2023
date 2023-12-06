import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Day2 {

    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "inputs", "day-2", "input.txt").toAbsolutePath();

        //Pt 1 constants
        final int redMax = 12;
        final int greenMax = 13;
        final int blueMax = 14;

        final var count1 = Files.lines(input)
                .map(Day2::toColorTotalMaxShown)
                .filter(t -> t.red <= redMax)
                .filter(t -> t.green <= greenMax)
                .filter(t -> t.blue <= blueMax)
                .mapToInt(t -> t.id)
                .sum();
        System.out.println("Part 1: " + count1);

        final var count2 = Files.lines(input)
                .map(Day2::toColorTotalMaxShown)
                .mapToInt(t -> t.red * t.green * t.blue)
                .sum();
        System.out.println("Part 2: " + count2);
    }

    static ColorTotal toColorTotalMin(String s) {
        final List<ColorTotal> totals = toColorTotals(s);
        final int mostRed = totals.stream().mapToInt(ColorTotal::red).min().orElseThrow();
        final int mostGreen = totals.stream().mapToInt(ColorTotal::green).min().orElseThrow();
        final int mostBlue = totals.stream().mapToInt(ColorTotal::blue).min().orElseThrow();
        return new ColorTotal(totals.get(0).id, mostRed, mostGreen, mostBlue);
    }

    // returns the max shown
    static ColorTotal toColorTotalMaxShown(String s) {
        final var totals = toColorTotals(s);
        final int mostRed = totals.stream().mapToInt(ColorTotal::red).max().orElseThrow();
        final int mostGreen = totals.stream().mapToInt(ColorTotal::green).max().orElseThrow();
        final int mostBlue = totals.stream().mapToInt(ColorTotal::blue).max().orElseThrow();
        return new ColorTotal(totals.get(0).id, mostRed, mostGreen, mostBlue);
    }

    // Returns the totals shown before put back into the bag
    static List<ColorTotal> toColorTotals(String s) {
        final var id = Integer.parseInt(s.split(" ")[1].replaceAll("\\D", ""));
        return Arrays.stream(s.replaceAll("Game \\d*: ", "").split("; ")) // split to put back in bag
                .map(s2 -> Arrays.stream(s2.split(", ")).map(Day2::toColorReveal).toList())
                .map(list -> list.stream().reduce(new ColorTotal(id, 0, 0, 0), Day2::accumulate, Day2::combine))
                .toList();
    }

    static ColorTotal combine(ColorTotal prev, ColorTotal next) {
        return new ColorTotal(prev.id, prev.red + next.red, prev.green + next.green, prev.blue + next.blue);
    }

    static ColorTotal accumulate(ColorTotal prev, ColorReveal i) {
        int r = prev.red;
        int g = prev.green;
        int b = prev.blue;
        switch (i.color) {
            case RED -> r += i.count;
            case GREEN -> g += i.count;
            case BLUE -> b += i.count;
        }
        return new ColorTotal(prev.id, r, g, b);
    }

    static ColorReveal toColorReveal(String s) {
        final var s1 = s.split(" ");
        return new ColorReveal(Integer.parseInt(s1[0]), Color.fromString(s1[1]));
    }

    record ColorReveal(int count, Color color) {

    }

    record ColorTotal(int id, int red, int green, int blue) {

    }

    enum Color {
        RED,
        GREEN,
        BLUE;

        public static Color fromString(String s) {
            return Arrays.stream(Color.values())
                    .filter(c -> c.name().toLowerCase().equals(s))
                    .findFirst()
                    .orElseThrow();
        }
    }

}
