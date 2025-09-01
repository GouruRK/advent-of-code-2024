package fr.aoc.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class AdventOfCode10P1 {

  private static final String EXAMPLE = """
          89010123
          78121874
          87430965
          96549874
          45678903
          32019012
          01329801
          10456732
          """;
  
  private record Coord(int x, int y) {
    public Coord add(Coord c) {
      return new Coord(c.x + x, c.y + y);
    }
  }
  
  private record Grid(int[][] grid) {
    public boolean isInGrid(Coord c) {
      return 0 <= c.x && c.x < grid[0].length && 0 <= c.y && c.y < grid.length;
    }
    
    public int get(Coord c) {
      return grid[c.y][c.x];
    }
    
    public int height() {
      return grid.length;
    }
    
    public int width() {
      return grid[0].length;
    }
  }
  
  private static Coord[] DIR = {
      new Coord(-1, 0),
      new Coord(1, 0),
      new Coord(0, -1),
      new Coord(0, 1)
  }; 
  
  
  private static int solve(Coord coord, Grid grid, HashSet<Coord> seen, int next) {
    if (!seen.add(coord)) {
      return 0;
    }
    if (grid.get(coord) == 9 && next == 10) {
      return 1;
    }
    return Arrays.stream(DIR)
        .map(d -> coord.add(d))
        .filter(grid::isInGrid)
        .filter(c -> grid.get(c) == next)
        .mapToInt(c -> solve(c, grid, seen, next + 1))
        .sum();
  }
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;
    
    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n"));;
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode10P1.java [FILE ?]");
      return;
    }
    
    var grid = new Grid(lines.map(str -> str.chars().map(c -> c - '0').toArray()).toArray(int[][]::new));
    
    var starting = IntStream.range(0, grid.height())
        .mapToObj(y -> IntStream.range(0, grid.width())
            .mapToObj(x -> new Coord(x, y)))
        .flatMap(Function.identity())
        .filter(c ->  grid.get(c) == 0)
        .collect(Collectors.toSet());
    
    System.out.println(starting.stream().mapToInt(c -> solve(c, grid, new HashSet<Coord>(), 1)).sum());
  }
  
  
}