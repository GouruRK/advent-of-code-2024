package fr.aoc.day18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AdventOfCode18P2 {
   
 
  // Idée : prendre le problème à l'envers : on commence avec la grille pleine
  // on crée un UnionFind avec les chemins possibles
  // on merge au fur et à mesure
  // une fois que find(start) == end, on a le chemin
  
  private static final int EXAMPLE_TRESHOLD = 12;
  private static final int INPUT_TRESHOLD = 1024;
  
  private static final String EXAMPLE = """
          5,4
          4,2
          4,5
          3,0
          2,1
          6,3
          2,4
          1,5
          0,6
          3,3
          2,6
          5,1
          1,2
          5,5
          2,5
          6,5
          1,4
          0,4
          6,4
          1,1
          6,1
          1,0
          0,5
          1,6
          2,0
          """;
 
  
  record Coord(int x, int y) {
    public Coord inverse() {
      return new Coord(-x, -y);
    }
    
    public Coord add(Coord other) {
      return new Coord(x + other.x, y + other.y);
    }
    
    public static Coord fromString(String input)  {
      var temp = input.split(",");
      return new Coord(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
    }
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
  
  record BuildPath(Coord current, int length) {}
  
  public static int breadth_search(Coord start, Coord end, Set<Coord> walls, int size) {
    var seen = new HashSet<Coord>();
    var queue = new ArrayDeque<BuildPath>(64);
    queue.add(new BuildPath(start, 0));
    
    while (!queue.isEmpty()) {
      var current = queue.removeFirst();
      if (current.current.equals(end)) {
        return current.length;
      }
      if (!seen.add(current.current)) {
        continue;
      }
      
      Arrays.stream(Direction.values())
        .map(dir -> dir.coord.add(current.current))
        .filter(c -> 0 <= c.x && c.x <= size && 0 <= c.y && c.y <= size)
        .filter(c -> !walls.contains(c))
        .forEach(c -> queue.addLast(new BuildPath(c, current.length + 1)));
    }
    return -1;
  }
  
  public static void main(String[] args) throws IOException {
    List<String> lines;
    int size, treshold;
    
    if (args.length == 0) {
      lines = EXAMPLE.strip().lines().toList();
      size = 6;
      treshold = EXAMPLE_TRESHOLD;
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
      size = 70;
      treshold = INPUT_TRESHOLD;
    } else {
      System.err.println("Usage: java AdventOfCode18P2.java [FILE ?]");
      return;
    }
    
    var walls = lines.stream().limit(treshold).map(Coord::fromString).collect(Collectors.toSet());
    int length, index = treshold;
    var start = new Coord(0, 0);
    var end = new Coord(size, size);
    
    do {
      walls.add(Coord.fromString(lines.get(index++)));
      length = breadth_search(start, end, walls, size);
    } while (length != -1);
    System.out.println(lines.get(index - 1));
  }
}