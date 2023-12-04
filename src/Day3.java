import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Day3 {

    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "day-3", "input.txt").toAbsolutePath();


        final var strings = Files.readAllLines(input);
        final var charGrid = toCharGrid(strings);
        final int maxY = charGrid.size();
        final int maxX = charGrid.get(0).size();
        int sum = 0;

        for (int y = 0; y < maxY; y++) {
            int lastSum = 0;
            for (int x = 0; x < maxX; x++) {
                final var start = new Coordinate(x, y);
                final var startChar = resolveCoordinate(start, charGrid);
                final int i = findPartNumber(start, startChar, maxX, maxY, charGrid, strings).orElse(0);

                if(lastSum != i) {
                    lastSum = i;
                    sum += i;
                }
                if(isLegal(right(start), maxX, maxY) && !Character.isDigit(resolveCoordinate(right(start), charGrid))) {
                    lastSum = 0; // make sure we allow duplicates if we have reached the end of a number
                }

            }
        }


        System.out.println("Part 1: " + sum);

        int gearSum = 0;

        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                final var start = new Coordinate(x, y);
                final var startChar = resolveCoordinate(start, charGrid);
                if(startChar == '*') {
                    final var digitNeighbors = lookUps(start)
                            .filter(c -> isLegal(c, maxX, maxY))
                            .filter(c -> Character.isDigit(resolveCoordinate(c, charGrid)))
                            .map(c -> findPartNumber(c, resolveCoordinate(c, charGrid), maxX, maxY, charGrid, strings))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .distinct()
                            .toList();

                    if(digitNeighbors.size() == 2) {
                        gearSum += digitNeighbors.get(0) * digitNeighbors.get(1);
                    }
                }
            }
        }

        System.out.println("Part 2: " + gearSum);

    }

    static Optional<Integer> findPartNumber(Coordinate start, char startChar, int maxX, int maxY, List<List<Character>> charGrid, List<String> strings) {
        return lookUps(start)
                .filter(c -> isLegal(c, maxX, maxY))
                .filter(c -> isSymbol(resolveCoordinate(c, charGrid)))
                .filter(c -> startChar != '.')
                .filter(c -> !isSymbol(startChar))
                .map( c -> integerFromCoordinate(start, strings.get(start.y)))
                .findFirst();
    }

    static int integerFromCoordinate(Coordinate c, String line) {
        int endEx = line.length();
        int start = 0;
        for (int i = c.x; i < line.length(); i++) {
            if(!Character.isDigit(line.charAt(i))) {
                endEx = i;
                break;
            }
        }

        for (int i = c.x; i >= 0; i--) {
            if(!Character.isDigit(line.charAt(i))) {
                start = i + 1;
                break;
            }
        }
        return Integer.parseInt(line.substring(start, endEx));
    }

    static char resolveCoordinate(Coordinate c, List<List<Character>> charGrid) {
        return charGrid.get(c.y).get(c.x);
    }

    static Stream<Coordinate> lookUps(Coordinate start) {
        return Stream.of(
                up(start),
                down(start),
                left(start),
                right(start),
                upLeft(start),
                upRight(start),
                downLeft(start),
                downRight(start)
        );
    }

    static List<List<Character>> toCharGrid(List<String> strings) {
        return strings.stream()
                .map(s -> s.chars().mapToObj(c -> (char) c).toList())
                .toList();
    }

    static Coordinate up(Coordinate in) {
        return new Coordinate(in.x, in.y - 1);
    }
    static Coordinate down(Coordinate in) {
        return new Coordinate(in.x, in.y  + 1);
    }
    static Coordinate left(Coordinate in) {
        return new Coordinate(in.x - 1, in.y);
    }
    static Coordinate right(Coordinate in) {
        return new Coordinate(in.x + 1, in.y);
    }
    static Coordinate upRight(Coordinate in) {
        return up(right(in));
    }
    static Coordinate upLeft(Coordinate in) {
        return up(left(in));
    }
    static Coordinate downRight(Coordinate in) {
        return down(right(in));
    }
    static Coordinate downLeft(Coordinate in) {
        return down(left(in));
    }

    static boolean isLegal(Coordinate c, int maxX, int maxY) {
        return c.x >= 0 && c.x < maxX && c.y >= 0 && c.y < maxY;
    }

    static boolean isSymbol(char c) {
        return !(c + "").matches("\\.|\\d");
    }

    record MyPair(Coordinate c, int partNumber){}
    record Coordinate(int x, int y) {}
}
