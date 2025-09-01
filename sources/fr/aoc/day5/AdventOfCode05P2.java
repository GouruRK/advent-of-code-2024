package fr.aoc.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AdventOfCode05P2 {

  private static final String EXAMPLE = """
      47|53
      97|13
      97|61
      97|47
      75|29
      61|13
      75|53
      29|13
      97|29
      53|29
      61|53
      97|53
      61|29
      47|13
      75|47
      97|75
      47|61
      75|61
      47|29
      75|13
      53|13
      
      75,47,61,53,29
      97,61,53,29,13
      75,29,13
      75,97,47,61,53
      61,13,29
      97,13,75,29,47
      """;

  private static Map<Integer, Set<Integer>> buildGraph(Map<Integer, Set<Integer>> allDependencies, List<Integer> source) {
    HashMap<Integer, Set<Integer>> graph = new HashMap<Integer, Set<Integer>>();
    
    for (var vertex: source) {
      for (var parentEntry: allDependencies.entrySet()) {
        if (parentEntry.getValue().contains(vertex) && source.contains(parentEntry.getKey())) {
          graph.compute(parentEntry.getKey(), (_, v) -> {
            if (v == null) {
              v = new HashSet<Integer>();
            }
            v.add(vertex);
            return v;
          });
        }
      }
    }
    for (var vertex: source) {
      graph.putIfAbsent(vertex, Set.of());
    }
    return graph;
  }
  
  private static List<Integer> topologicalSort(Map<Integer, Set<Integer>> graph) {
    // associate each vertex with their number of "children"
    HashMap<Integer, Integer> indeg = new HashMap<>();
    for (var vertex: graph.keySet()) {
      for (var neighbours: graph.values()) {
        if (neighbours.contains(vertex)) {
          indeg.compute(vertex, (_, v) -> (v == null ? 0: v) + 1);
        }
      }
      indeg.putIfAbsent(vertex, 0);
    }
    
    // create a queue that contains the vertecies which have no parents  
    var sources = indeg.entrySet().stream()
        .filter(entry -> entry.getValue() == 0)
        .map(entry -> entry.getKey())
        .collect(Collectors.toCollection(ArrayDeque::new));
    
    // result
    ArrayList<Integer> res = new ArrayList<>();
    
    // topological sort
    while (!sources.isEmpty()) {
      int vertex = sources.removeLast();
      if (res.contains(vertex)) {
        continue;
      }
      res.add(vertex);
      for (var children: graph.get(vertex)) {
        indeg.compute(children, (_, v) -> (v == null ? 0: v) - 1);
      }
      sources.addAll(indeg.entrySet().stream()
          .filter(entry -> entry.getValue() == 0)
          .map(entry -> entry.getKey())
          .collect(Collectors.toCollection(ArrayDeque::new)));
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
      System.err.println("Usage: java AdventOfCode05P2.java [FILE ?]");
      return;
    }
    int breakLine = lines.indexOf("");
    
    Map<Integer, Set<Integer>> dependencies = lines.stream()
        .limit(breakLine)
        .map(s -> s.split("\\|"))
        .collect(Collectors.groupingBy(t -> Integer.parseInt(t[0]), Collectors.mapping(t -> Integer.parseInt(t[1]), Collectors.toSet())));
    
    var unOrderedLines = IntStream.range(breakLine + 1, lines.size())
        .mapToObj(lines::get)
        .map(s -> Arrays.stream(s.split(",")).map(n -> Integer.parseInt(n)).toList())
        .filter(line -> !IntStream.range(1, line.size())
            .allMatch(i -> !dependencies.computeIfAbsent(line.get(i), _ -> new HashSet<>()).contains(line.get(i - 1))))
        .toList();
    
    var res = unOrderedLines.stream()
      .map(line -> buildGraph(dependencies, line))
      .map(AdventOfCode05P2::topologicalSort)
      .mapToInt(l -> l.get(l.size() / 2))
      .sum();
    
    System.out.println(res);
  }
  
}
