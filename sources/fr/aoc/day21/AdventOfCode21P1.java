package fr.aoc.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public final class AdventOfCode21P1 {
  private static final String EXAMPLE = """
      029A
      980A
      179A
      456A
      379A
      """;

  
  private static final char[][] RAW_NUMERIC_KEYPAD = {
      { '7', '8', '9', },
      { '4', '5', '6', },
      { '1', '2', '3', },
      { ' ', '0', 'A', },
  };
  
  private static final char[][] RAW_DIRECTIONAL_KEYPAD = {
      { ' ', '^', 'A', },
      { '<', 'v', '>', },
  };
  
  private static final Keypad NUMERIC_KEYPAD = createKeypad(RAW_NUMERIC_KEYPAD).buildRoutes();
  private static final Keypad DIRECTIONAL_KEYPAD = createKeypad(RAW_DIRECTIONAL_KEYPAD).buildRoutes();
  
  
  enum Direction {
    NORTH(new Coord(0, -1), '^'),
    SOUTH(new Coord(0, 1), 'v'),
    WEST(new Coord(-1, 0), '<'),
    EAST(new Coord(1, 0), '>');
    
    private final Coord coord;
    private final char symbol;
    
    private Direction(Coord coord, char symbol) {
      this.coord = coord;
      this.symbol = symbol;
    }
  }
  
  private record Edge(char from, char to) {}
  
  static class Keypad {
    record PathBuilder(Coord coord, Set<Coord> seen, String path) {
     
    }

    private final char[][] keypad;
    private final Map<Character, Coord> items;
    private final HashMap<Edge, ArrayList<String>> routes = new HashMap<>();
  
    public Keypad(char[][] keypad, Map<Character, Coord> items) {
      this.keypad = keypad; 
      this.items = items;
    }

    private Stream<PathBuilder> nextCoords(PathBuilder current) {
      return Arrays.stream(Direction.values())
          .filter(dir -> !current.seen.contains(dir.coord.add(current.coord)))
          .map(dir -> {
            var set = current.seen.stream().collect(Collectors.toSet());
            var nextCoord = dir.coord.add(current.coord); 
            set.add(nextCoord);
            return new PathBuilder(nextCoord, set, current.path + dir.symbol);
          })
          .filter(p -> 0 <= p.coord.y && p.coord.y < keypad.length && 0 <= p.coord.x && p.coord.x < keypad[0].length)
          .filter(p -> keypad[p.coord.y][p.coord.x] != ' ');
    }
    
    public Keypad buildRoutes() {
      items.values().stream().forEach(from -> items.values().stream().forEach(end -> buildRoute(from, end)));
      for (var route: routes.values()) {
        var minLength = route.stream().mapToInt(String::length).min().getAsInt();
        route.removeIf(r -> r.length() != minLength);
        
      }
      return this;
    }
    
    private void buildRoute(Coord from, Coord end) {
      var queue = new ArrayDeque<PathBuilder>();
      queue.add(new PathBuilder(from, new HashSet<>(), ""));
      
      while (!queue.isEmpty()) {
        var path = queue.removeFirst();
        if (path.coord.equals(end)) {
          routes.computeIfAbsent(new Edge(keypad[from.y][from.x], keypad[end.y][end.x]), _ -> new ArrayList<>()).add(path.path + "A");
          continue;
        }
        nextCoords(path).forEach(queue::add);
      }
    }
  }
  
  
  record Coord(int x, int y) {
    public Coord inverse() {
      return new Coord(-x, -y);
    }
    
    public Coord add(Coord other) {
      return new Coord(x + other.x, y + other.y);
    }
  }
  
  static record Robot(Keypad keypad, Coord position) {
    public Robot(Keypad keypad) {
      this(keypad, keypad.items.get('A'));
    }
    
    public List<String> goTo(char c) {
      return keypad.routes.get(new Edge(keypad.keypad[position.y][position.x], c));
    }
    
    public Coord nextPosition(char c) {
      return keypad.items.get(c);
    }
    
    public Robot clone() {
      return new Robot(keypad, new Coord(position.x, position.y));
    }
    
    public Robot changePosition(char c) {
      return new Robot(keypad, nextPosition(c));
    }
    
  }
  
  private static Keypad createKeypad(char[][] rawKeypad) {
    var items = IntStream.range(0, rawKeypad.length)
      .mapToObj(y -> IntStream.range(0, rawKeypad[y].length).mapToObj(x -> new Coord(x, y)))
      .flatMap(x -> x)
      .filter(coord -> rawKeypad[coord.y][coord.x] != ' ')
      .collect(Collectors.toMap(c -> rawKeypad[c.y][c.x], Function.identity()));
    return new Keypad(rawKeypad, items);
  }
  
  
  record Robots(Robot[] robots) {
    public Robots() {
      this(new Robot[] { new Robot(NUMERIC_KEYPAD), new Robot(DIRECTIONAL_KEYPAD), new Robot(DIRECTIONAL_KEYPAD) });
    }
    
    public Robots clone() {
      return new Robots(new Robot[] { robots[0].clone(), robots[1].clone(), robots[2].clone() });
    }
    
    public Robots replace(Robot robot, int index) {
      return switch (index) {
      case 0 -> new Robots(new Robot[] { robot, robots[1], robots[2] } );
      case 1 -> new Robots(new Robot[] { robots[0], robot, robots[2] } );
      default -> new Robots(new Robot[] { robots[0], robots[1], robot } );
      };
    }
    
  }

  record Tuple(Robots robots, String output) {
    
  }
  
  
  private static List<Tuple> writeChar(Robots robots, char c, int index) {
    var res = new ArrayList<Tuple>();
    var robot = robots.robots[index];
    for (var t: robot.goTo(c)) {
      res.add(new Tuple(robots.replace(robot.changePosition(c), index), t));
    }
    return res;
  }

  private static List<Tuple> writeString(Robots robots, String string, int index) {
    if (index == robots.robots.length) {
      return List.of(new Tuple(robots, string));
    }
    
    var res = List.of(new Tuple(robots, ""));
    
    for (int i = 0; i < string.length(); i++) {
      var newRes = new ArrayList<Tuple>();
      for (var previousChar: res) {
        for (var tuple: writeChar(previousChar.robots, string.charAt(i), index)) {
          for (var t: writeString(tuple.robots, tuple.output, index + 1)) {
            newRes.add(new Tuple(t.robots, previousChar.output + t.output));                
          }
        }        
      }
      res = newRes;
    }
    return res;
  }
  
  public static void main(String[] args) throws IOException {
    List<String> lines;
    var pattern = Pattern.compile("(\\d+)");
    
    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n")).toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode21P1.java [FILE ?]");
      return;
    }
      
    long sum = 0;
    for (var line: lines) {
      var base = pattern.matcher(line).results().mapToInt(r -> Integer.parseInt(r.group())).findFirst().getAsInt();
      var tuple = writeString(new Robots(), line, 0).stream().min(Comparator.comparing(t -> t.output.length())).get();
      sum += base*tuple.output.length();
    }
    System.out.println(sum);
  }
}
