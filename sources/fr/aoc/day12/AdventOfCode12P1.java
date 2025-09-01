package fr.aoc.day12;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

public final class AdventOfCode12P1 {

  private static final String EXAMPLE = """
          AAAA
          BBCD
          BBCC
          EEEC
          """;
  
  private static final String EXAMPLE2 = """
          OOOOO
          OXOXO
          OOOOO
          OXOXO
          OOOOO
          """;
  
  private static final String EXAMPLE3 = """
          RRRRIICCFF
          RRRRIICCCF
          VVRRRCCFFF
          VVRCCCJFFF
          VVVVCJJCFE
          VVIVCCJJEE
          VVIIICJJEE
          MIIIIIJJEE
          MIIISIJEEE
          MMMISSJEEE
          """;
  
  static class Grid {
    private final int[][] grid;
    private final int width; 
    private final int height;
    
    public Grid(int[][] grid) {
      this.grid = grid;
      this.width = grid[0].length;
      this.height = grid.length;
    }
    
    int get(Coord coord) {
      return grid[coord.y][coord.x];
    }
    
    int get(int x, int y) {
      return grid[y][x];
    }
    
    boolean isInGrid(Coord coord) {
      return 0 <= coord.x && coord.x < width && 0 <= coord.y && coord.y < height;
    }
    
    void setVisited(Coord coord) {
      grid[coord.y][coord.x] = -grid[coord.y][coord.x];
    }
    
    public void print() {
      for (var line: grid) {
        System.out.println(Arrays.toString(line));
      }
    }
  }
  
  static record Coord(int x, int y) {
    Coord next(Coord other) {
      return new Coord(x + other.x, y + other.y);
    }
  }
  
  record Data(int area, int perimeter) {
    public Data merge(Data other) {
      return new Data(area + other.area, perimeter + other.perimeter);
    }
    
    int value() {
      return area*perimeter;
    }
  }
  
  private static final Coord[] DIRS = {
    new Coord(-1, 0),
    new Coord(1, 0),
    new Coord(0, -1),
    new Coord(0, 1),
  };
  
  public static Data exploreCluster(Grid grid, Coord coord) {
    var neighbours = Arrays.stream(DIRS)
        .map(coord::next)
        .filter(grid::isInGrid)
        .filter(c -> grid.get(c) == grid.get(coord))
        .toList();
    
    var oldNeighbours = Arrays.stream(DIRS)
        .map(coord::next)
        .filter(grid::isInGrid)
        .filter(c -> grid.get(c) == -grid.get(coord))
        .count();
        
    grid.setVisited(coord);
    
    var data = new Data(1, 4 - (neighbours.size() + (int) oldNeighbours));
    for (var n: neighbours) {
      if (grid.get(n) < 0) {
        continue;
      }
      data = data.merge(exploreCluster(grid, n));
    }
    return data;
  }
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;
    
    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE3.strip().split("\n"));;
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode12P1.java [FILE ?]");
      return;
    }
    var grid = new Grid(lines.map(line -> line.chars().toArray()).toArray(int[][]::new));
    
    int acc = 0;
    for (int y = 0; y < grid.height; y++) {
      for (int x = 0; x < grid.width; x++) {
        if (grid.get(x, y) > 0) {
          acc += exploreCluster(grid, new Coord(x, y)).value();
        }
      }
    }
    System.out.println(acc);
  }
  
  
}