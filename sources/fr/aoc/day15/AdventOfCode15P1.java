package fr.aoc.day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AdventOfCode15P1 {
  
  private static final String EXAMPLE = """
          ########
          #..O.O.#
          ##@.O..#
          #...O..#
          #.#.O..#
          #...O..#
          #......#
          ########
          
          <^^>>>vv<v>>v<<
          """;
 
  private static final String EXAMPLE2 = """
          ##########
          #..O..O.O#
          #......O.#
          #.OO..O.O#
          #..O@..O.#
          #O#..O...#
          #O..O..O.#
          #.OO.O.OO#
          #....O...#
          ##########
          
          <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
          vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
          ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
          <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
          ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
          ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
          >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
          <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
          ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
          v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
          """;

  record Coord(int x, int y) {
    static Coord from(int c) {
      return switch (c) {
        case '<' -> new Coord(-1, 0);
        case '>' -> new Coord(1, 0);
        case '^' -> new Coord(0, -1);
        case 'v' -> new Coord(0, 1);
        default -> null;
      };
      
    }
    Coord inverse() {
      return new Coord(-x, -y);
    }
    
    Coord add(Coord c) {
      return new Coord(x + c.x, y + c.y);
    }
  }
  
  record Grid(char[][] grid) {
    @Override
    public String toString() {
      return Arrays.stream(grid).map(String::valueOf).collect(Collectors.joining("\n"));
    }
    
    char onGrid(Coord c) {
      return grid[c.y][c.x];
    }
    
    char onGrid(int x, int y) {
      return grid[y][x];
    }
    
    int height() {
      return grid.length;
    }
    
    int width() {
      return grid[0].length;
    }
    
    void pushLine(Coord emptySpace, Coord currentPos, Coord nextPos) {
      switchPlace(currentPos, emptySpace);
      switchPlace(emptySpace, nextPos);
    }
    
    Coord hasEmptySpace(Coord from, Coord dir) {
      while (onGrid(from) != '#' && onGrid(from) != '.') {
        from = from.add(dir);
      }
      return onGrid(from) == '.' ? from: null;
    }
    
    void switchPlace(Coord from, Coord to) {
      var t = onGrid(from);
      grid[from.y][from.x] = grid[to.y][to.x];
      grid[to.y][to.x] = t;
    }
  }
  
  
  public static void main(String[] args) throws IOException {
    List<String> lines;

    if (args.length == 0) {
      lines = EXAMPLE.strip().lines().toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode15P1.java [FILE ?]");
      return;
    }
    int breakLine = lines.indexOf("");
    var grid = new Grid(lines.subList(0, breakLine).stream().map(line -> line.toCharArray()).toArray(char[][]::new));
    var movements = lines.subList(breakLine + 1, lines.size()).stream().flatMapToInt(line -> line.chars()).mapToObj(Coord::from).toArray(Coord[]::new);
    
    Coord robot = null;
    for (int y = 0; y < grid.height(); y++) {
      for (int x = 0; x < grid.height(); x++) {
        if (grid.onGrid(x, y) == '@') {
          robot = new Coord(x, y);
          break;
        }
      }
      if (robot != null) {
        break;
      }
    }
        
    for (var dir: movements) {
      var nextRobot = robot.add(dir);
      switch (grid.onGrid(nextRobot)) {
      case '.' -> {
        grid.switchPlace(nextRobot, robot);
        robot = nextRobot;
      }
      case 'O' -> {
        var empty = grid.hasEmptySpace(robot, dir); 
        if (empty != null) {
          grid.pushLine(empty, robot, nextRobot);
          robot = nextRobot;
        }
      }
      default -> {}
      }
    }
    
    System.out.println(IntStream.range(0, grid.height()).map(y -> IntStream.range(0, grid.width()).filter(x -> grid.onGrid(x, y) == 'O').map(x -> y*100 + x).sum()).sum());
  }

}
