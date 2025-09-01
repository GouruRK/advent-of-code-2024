package fr.aoc.day20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Stream;


public final class AdventOfCode20P1 {

  private static final String EXAMPLE = """
      ###############
      #...#...#.....#
      #.#.#.#.#.###.#
      #S#...#.#.#...#
      #######.#.#.###
      #######.#.#...#
      #######.#.###.#
      ###..E#...#...#
      ###.#######.###
      #...###...#...#
      #.#####.#.###.#
      #.#...#.#.#...#
      #.#.#.#.#.#.###
      #...#...#...###
      ###############
      """;

  record Coord(int x, int y) {
    public Coord inverse() {
      return new Coord(-x, -y);
    }
    
    public Coord add(Coord other) {
      return new Coord(x + other.x, y + other.y);
    }
  }
  
  private static Stream<Coord> nextCoords(int[][] grid, Coord current, Predicate<Coord> predicate) {
    return Arrays.stream(Direction.values())
        .map(dir -> current.add(dir.coord))
        .filter(predicate);
  }
  
  private static ArrayList<Coord> breadthSearch(int[][] grid, Coord start) {
    var queue = new ArrayDeque<Coord>();
    var onPath = new ArrayList<Coord>();
    queue.add(start);
    var last = start;
    while (!queue.isEmpty()) {
      var coord = queue.removeFirst();
      if (grid[coord.y][coord.x] != 0) {
        continue;
      }
      onPath.add(coord);
      nextCoords(grid, coord, c -> grid[c.y][c.x] == 0).forEach(queue::add);
      grid[coord.y][coord.x] = grid[last.y][last.x] + 1;
      last = coord;
    }
    return onPath;
  }
  
  enum Direction {
    NORTH(new Coord(0, -1)),
    SOUTH(new Coord(0, 1)),
    WEST(new Coord(-1, 0)),
    EAST(new Coord(1, 0));
    
    private final Coord coord;
    
    private Direction(Coord coord) {
      this.coord = coord;
    }
  }
  
  private static int shortCut(int[][] grid, ArrayList<Coord> onPath) {
    var seen = new HashSet<Coord>();
    var acc = 0;
    for (var coord: onPath) {
      acc += Arrays.stream(Direction.values()).map(dir -> dir.coord.add(dir.coord).add(coord))
        .filter(c -> (0 <= c.y && c.y < grid.length) && (0 <= c.x && c.x < grid[0].length))
        .filter(c -> grid[c.y][c.x] != -1)
        .filter(c -> !seen.contains(c))
        .filter(c -> grid[c.y][c.x] - grid[coord.y][coord.x] - 2 >= 100)
        .count();
    }
    return acc;
  }
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;
    
    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n"));
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode20P1.java [FILE ?]");
      return;
    }
    
    var grid = lines.map(line -> line.chars().toArray()).toArray(int[][]::new);
    Coord start = null;
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[y].length; x++) {
        switch (grid[y][x]) {
        case 'S' -> { start = new Coord(x, y); grid[y][x] = 0; }
        case '#' -> grid[y][x] = -1;
        default -> grid[y][x] = 0;
        }
      }
    }
    System.out.println(shortCut(grid, breadthSearch(grid, start)));
    // Arrays.stream(grid).map(line -> Arrays.stream(line).mapToObj(x -> " " + x).collect(Collectors.joining("\t"))).forEach(System.out::println);;    
  }
  
}
