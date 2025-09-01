package fr.aoc.day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AdventOfCode06P2 {

  private enum Direction {
    NORTH(new Pos(0, -1)),
    SOUTH(new Pos(0, 1)),
    EAST(new Pos(1, 0)),
    WEST(new Pos(-1, 0));

    private final Pos coords;
    
    private Direction(Pos coords) {
      this.coords = coords;
    }
    
    private static final Direction turn(Direction dir) {
      return switch (dir) {
      case Direction.NORTH -> Direction.EAST;
      case Direction.EAST -> Direction.SOUTH;
      case Direction.SOUTH -> Direction.WEST;
      case Direction.WEST -> Direction.NORTH;
      };
    }
    
  }
  
  private static final String EXAMPLE = """
          ....#.....
          .........#
          ..........
          ..#.......
          .......#..
          ..........
          .#..^.....
          ........#.
          #.........
          ......#...
          """;
  
  record Pos(int x, int y) {
    public Pos add(Direction dir) {
      return new Pos(x + dir.coords.x, y + dir.coords.y);
    }
  }
  
  private static boolean isInGrid(Pos pos, int width, int height) {
    return (0 <= pos.x && pos.x < width) && (0 <= pos.y && pos.y < height); 
  }
  
  private static HashSet<Pos> runGuard(Set<Pos> blocks, Pos pos, int width, int height) {
    HashSet<Pos> visited = new HashSet<Pos>();
    Direction dir = Direction.NORTH;
    
    Pos previousPos = pos;
    while (isInGrid(pos, width, height)) {
      visited.add(pos);
      previousPos = pos;
      
      pos = pos.add(dir);
      if (blocks.contains(pos)) {
        pos = previousPos;
        dir = Direction.turn(dir);        
      }
    }
    return visited;
  }
  
  record Entry(Pos pos, Direction dir) {}
   
  private static boolean isTrap(Set<Pos> blocks, Pos pos, int width, int height) {
    HashSet<Entry> blockEncountered = new HashSet<Entry>();
    Pos previousPos = pos;
    
    Direction dir = Direction.NORTH;
    while (isInGrid(pos, width, height)) {
      previousPos = pos;
      
      pos = pos.add(dir);
      if (blocks.contains(pos)) {
        if (blockEncountered.contains(new Entry(pos, dir))) {
          return true;
        }
        blockEncountered.add(new Entry(pos, dir));
        pos = previousPos;
        dir = Direction.turn(dir);        
      }
    }
    return false;
  }
  
  private static int solve(HashSet<Pos> blocks, Pos pos, int width, int height) {
    var path = runGuard(blocks, pos, width, height);
    path.remove(pos);
    
    int res = 0;
    for (var tile: path) {
      blocks.add(tile);
      if (isTrap(blocks, pos, width, height)) {
        res++;
      }
      blocks.remove(tile);
    }
    return res;
  }
  
  public static void main(String[] args) throws IOException {
    List<String> lines;
    
    if (args.length == 0) {
      lines = EXAMPLE.strip().lines().toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode06P2.java [FILE ?]");
      return;
    }
    
    var blocks = new HashSet<Pos>();
    Pos startingPos = null;
    
    for (int y = 0; y < lines.size(); y++) {
      for (int x = 0; x < lines.get(y).length(); x++) {
        switch (lines.get(y).charAt(x)) {
        case '^' -> startingPos = new Pos(x, y);
        case '#' -> blocks.add(new Pos(x, y));
        default -> {}
        }        
      }
    }
    System.out.println(solve(blocks, startingPos, lines.get(0).length(), lines.size()));
  }
  
}
