package fr.aoc.day16;

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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.aoc.day16.AdventOfCode16P1.Coord;
import fr.aoc.day16.AdventOfCode16P1.Direction;
import fr.aoc.day16.AdventOfCode16P1.Edge;
import fr.aoc.day16.AdventOfCode16P1.Graph;
import fr.aoc.day16.AdventOfCode16P1.Tuple;

public final class AdventOfCode16P2 {
    
  private static final String EXAMPLE = """
          ###############
          #.......#....E#
          #.#.###.#.###.#
          #.....#.#...#.#
          #.###.#####.#.#
          #.#.#.......#.#
          #.#.#####.###.#
          #...........#.#
          ###.#.#####.#.#
          #...#.....#.#.#
          #.#.#.###.#.#.#
          #.....#...#.#.#
          #.###.#.#.#.#.#
          #S..#.....#...#
          ###############
          """;
  
  private static final String EXAMPLE2 = """
          #################
          #...#...#...#..E#
          #.#.#.#.#.#.#.#.#
          #.#.#.#...#...#.#
          #.#.#.#.###.#.#.#
          #...#.#.#.....#.#
          #.#.#.#.#.#####.#
          #.#...#.#.#.....#
          #.#.#####.#.###.#
          #.#.#.......#...#
          #.#.###.#####.###
          #.#.#...#.....#.#
          #.#.#.#####.###.#
          #.#.#.........#.#
          #.#.#.#########.#
          #S#.............#
          #################
          """;
  
  record Coord(int x, int y) {
    public Coord inverse() {
      return new Coord(-x, -y);
    }
    
    public Coord add(Coord other) {
      return new Coord(x + other.x, y + other.y);
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
    
  record Edge(Tuple from, Tuple to, int value) {}
  
  
  record Tuple(Coord coord, Direction dir) {}
  
  record BestPath(int value, Set<Coord> coords, Tuple current) {
    public BestPath(Tuple current) {
      this(0, new HashSet<Coord>(), current);
    }
  }
  
  
  static class Graph {
    private final HashMap<Tuple, List<Edge>> graph = new HashMap<>();
    
    public void fillGraph(char[][] grid, Tuple start) {
      var seen = new HashSet<Tuple>();
      var queue = new ArrayDeque<Tuple>();
      queue.add(start);
      
      while (!queue.isEmpty()) {
        var tuple = queue.removeFirst();
        if (!seen.add(tuple)) {
          continue;
        }
        
        Arrays.stream(Direction.values())
          .map(dir -> new Tuple(tuple.coord.add(dir.coord), dir))
          .filter(t -> grid[t.coord.y][t.coord.x] != '#')
          .filter(t -> !t.dir.coord.equals(tuple.dir.coord.inverse()))
          .peek(queue::add)
          .forEach(t -> {
            graph.computeIfAbsent(tuple, _ -> new ArrayList<>()).add(new Edge(tuple, t, tuple.dir.equals(t.dir) ? 1: 1001));
          });
      }
    }
  }
  
  private static void updateDistances(HashMap<Tuple, Integer> distances, Graph graph, Tuple tuple) {
    graph.graph.getOrDefault(tuple, new ArrayList<Edge>()).forEach(edge -> {
      distances.compute(edge.to, (k, v) -> {
        if (v == null) {
          return edge.value + distances.get(tuple);
        }
        return Math.min(v, distances.get(tuple) + edge.value);
      });
    });
  }
  
  private static Tuple findNextTuple(HashMap<Tuple, Integer> distances, HashSet<Tuple> seen) {
    return distances.entrySet().stream()
        .filter(entry -> !seen.contains(entry.getKey()))
        .min(Comparator.comparingInt(entry -> entry.getValue()))
        .get().getKey();
  }
  
  private static HashMap<Tuple, Integer> dijkstra(Graph graph, Tuple start, Coord exit) {
    var distances = new HashMap<Tuple, Integer>();
    distances.put(start, 0);
    var seen = new HashSet<Tuple>();
    
    var tuple = start;
    
    while (!tuple.coord.equals(exit)) {
      seen.add(tuple);
      updateDistances(distances, graph, tuple);
      tuple = findNextTuple(distances, seen);
    }
    return distances;
  }
  
  
  private static Stream<BestPath> nextPaths(Graph graph, BestPath path) {
    return graph.graph.getOrDefault(path.current, new ArrayList<Edge>()).stream()
      .filter(edge -> !path.coords.contains(edge.to.coord))
      .map(edge -> {
        var pos = new HashSet<Coord>(path.coords);
        pos.add(edge.to.coord);
        return new BestPath(path.value + edge.value, pos, edge.to);
      });
  }
  
  private static List<BestPath> allPaths(Graph graph, Tuple start, Coord exit) {
    var bestPaths = new ArrayList<BestPath>();
    var queue = new ArrayDeque<BestPath>();
    var distances = dijkstra(graph, start, exit);
    var bestScore = distances.getOrDefault(new Tuple(exit, Direction.NORTH), distances.get(new Tuple(exit, Direction.EAST)));
    
    queue.add(new BestPath(start));
    
    while (!queue.isEmpty()) {
      var path = queue.removeFirst();
      if (path.current.coord.equals(exit) && path.value == bestScore) {
        bestPaths.add(path);
        continue;
      }
      if (path.value > bestScore) {
        continue;
      }
      nextPaths(graph, path).forEach(queue::add);
    }
    
    return bestPaths;
  }

 
  public static int commonCells(List<Set<Coord>> coords) {
    return coords.stream().flatMap(Set::stream).collect(Collectors.toSet()).size() + 1;
  }
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;

    if (args.length == 0) {
      lines = EXAMPLE.strip().lines();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode16P2.java [FILE ?]");
      return;
    }
    
    var grid = lines.map(String::toCharArray).toArray(char[][]::new);
    var height = grid.length;
    var width = grid[0].length;
    var start = new Tuple(new Coord(1, height - 2), Direction.EAST);
    var exit = new Coord(width - 2, 1);
    var graph = new Graph();
    graph.fillGraph(grid, start);
    var res = allPaths(graph, start, exit);
    System.out.println(res.size());
    System.out.println(commonCells(res.stream().map(BestPath::coords).toList()));
  }
}
