
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day10 extends AOCBase {

    private List<Coordinate> path = null;
    protected Day10() {
        super("day-10", "input.txt");
    }

    public static void main(String[] args) throws Exception {
        new Day10().run();
    }

    @Override
    void part1(Stream<String> fileInput) throws Exception {
        final char[][] grid = fileInput.map(String::toCharArray)
                .toArray(char[][]::new);
        final MutableGraph<Coordinate> graph = GraphBuilder.directed().allowsSelfLoops(true).expectedNodeCount(grid.length * grid[0].length).build();

        Coordinate start = null;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                final var coordinate = new Coordinate(x, y);
                graph.addNode(coordinate);
                findNext(grid, coordinate).forEach(n -> graph.putEdge(coordinate, n));
                if (grid[y][x] == Connection.START.getRep()) {
                    start = new Coordinate(x, y);
                }
            }
        }
        path = new ArrayList<>();
        final var iterator = Traverser.forGraph(graph).depthFirstPreOrder(start).iterator();
        iterator.next(); // skip start

        iterator.forEachRemaining(path::add);

        System.out.println("Result Part 1: " +  Math.ceilDiv(path.size(), 2));

    }

    @Override
    void part2(Stream<String> fileInput) throws Exception {
        final char[][] grid = fileInput.map(String::toCharArray)
                .toArray(char[][]::new);

        int pipeCrossings = 0;
        char lastVertical = '0';
        List<Coordinate> interior = new ArrayList<>();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                final var coordinate = new Coordinate(x, y);
                final var value = valueAt(grid, coordinate);
                if (path.contains(coordinate)) {
                    if(isCrossing(value, lastVertical)) {
                        pipeCrossings++;
                    }

                    if (value != '-') {
                        lastVertical = value;
                    }

                } else if(pipeCrossings % 2 == 1) {
                    interior.add(coordinate);
                }
            }
            pipeCrossings = 0;
            lastVertical = '0';
        }

        System.out.println("Result Part 2: " +  interior.size());

    }


    static boolean isCrossing(char value, char lastVertical) {
        return (value == '|' || value == 'S')
                || (value == '7' && lastVertical == 'L')
                || (value == 'J' && lastVertical == 'F');
    }

    static List<Coordinate> findNext(char[][] grid, Coordinate current) {
        return lookUps(current)
                .filter(c -> isLegal(c, grid))
                .filter(next -> connects(grid, current, next))
                .toList();
    }

    static char valueAt(char[][] grid, Coordinate coordinate) {
        return grid[coordinate.y()][coordinate.x()];
    }

    static boolean connects(char[][] grid, Coordinate current, Coordinate candidate) {
        final var rep = valueAt(grid, candidate);
        final var candidateConn = Connection.fromChar(rep);
        final var currentConn = Connection.fromChar(valueAt(grid, current));

        return candidateConn != Connection.GROUND && currentConn.connectsTo(Dir.from(current, candidate).orElseThrow(), rep);

    }

    static Coordinate scanFor(char[][] grid, Predicate<Character> cond) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                final var c = grid[y][x];
                if (cond.test(c)) {
                    return new Coordinate(x, y);
                }
            }
        }
        throw new IllegalStateException();
    }

    static Stream<Coordinate> lookUps(Coordinate start) {
        return Stream.of(
                north(start),
                south(start),
                east(start),
                west(start)
        );
    }

    static Coordinate north(Coordinate in) {
        return new Coordinate(in.x(), in.y() - 1);
    }

    static Coordinate south(Coordinate in) {
        return new Coordinate(in.x(), in.y() + 1);
    }

    static Coordinate east(Coordinate in) {
        return new Coordinate(in.x() + 1, in.y());
    }

    static Coordinate west(Coordinate in) {
        return new Coordinate(in.x() - 1, in.y());
    }

    static boolean isLegal(Coordinate c, char[][] grid) {
        return c.x() >= 0 && c.x() < grid[0].length
                && c.y() >= 0 && c.y() < grid.length;
    }

    enum Connection {
        V_PIPE('|', true, Map.of(
                Dir.N, List.of('|', 'F', '7'),
                Dir.S, List.of('|', 'L', 'J'),
                Dir.E, List.of(),
                Dir.W, List.of()
        )),
        H_PIPE('-', true, Map.of(
                Dir.N, List.of(),
                Dir.S, List.of(),
                Dir.E, List.of('-', 'J', '7'),
                Dir.W, List.of('-', 'L', 'F')
        )),
        NE_BEND('L', true, Map.of(
                Dir.N, List.of('|', 'F', '7'),
                Dir.S, List.of(),
                Dir.E, List.of('-', 'J', '7'),
                Dir.W, List.of()
        )),
        NW_BEND('J', true, Map.of(
                Dir.N, List.of('|', 'F', '7'),
                Dir.S, List.of(),
                Dir.E, List.of(),
                Dir.W, List.of('-', 'L', 'F')
        )),
        SW_BEND('7', true, Map.of(
                Dir.N, List.of(),
                Dir.S, List.of('|', 'L', 'J'),
                Dir.E, List.of(),
                Dir.W, List.of('-', 'L', 'F')
        )),
        SE_BEND('F', true, Map.of(
                Dir.N, List.of(),
                Dir.S, List.of('|', 'L', 'J'),
                Dir.E, List.of('-', 'J', '7'),
                Dir.W, List.of()
        )),
        GROUND('.', false, Map.of(
                Dir.N, List.of(),
                Dir.S, List.of(),
                Dir.E, List.of(),
                Dir.W, List.of()
        )),
        START('S', true, Map.of(
                Dir.N, List.of('|', 'F', '7'),
                Dir.S, List.of('|', 'L', 'J'),
                Dir.E, List.of('-', 'J', '7'),
                Dir.W, List.of('-', 'L', 'F')
        ));

        final char rep;
        final boolean isPipe;
        final Map<Dir, List<Character>> connections;

        char getRep() {
            return rep;
        }

        char isPipe() {
            return rep;
        }

        boolean connectsTo(Dir dir, char c) {
            return this.connections.get(dir).contains(c) || c == START.getRep();
        }

        static Connection fromChar(char from) {
            return Arrays.stream(Connection.values())
                    .filter(c -> c.getRep() == from)
                    .findFirst()
                    .orElseThrow();
        }

        Connection(char rep, boolean isPipe, Map<Dir, List<Character>> connections) {
            this.rep = rep;
            this.isPipe = isPipe;
            this.connections = connections;
        }
    }

    enum Dir {
        N,
        S,
        E,
        W;

        static Optional<Dir> from(Coordinate current, Coordinate next) {
            if (north(current).equals(next)) {
                return Optional.of(N);
            }
            if (south(current).equals(next)) {
                return Optional.of(S);
            }
            if (east(current).equals(next)) {
                return Optional.of(E);
            }
            if (west(current).equals(next)) {
                return Optional.of(W);
            }

            return Optional.empty();
        }
    }

}