package fr.aoc.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AdventOfCode05P1 {

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

  
  public static void main(String[] args) throws IOException {
    List<String> lines;
    
    if (args.length == 0) {
      lines = EXAMPLE.strip().lines().toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode05P1.java [FILE ?]");
      return;
    }
    int breakLine = lines.indexOf("");
    
    Map<Integer, Set<Integer>> dependencies = lines.stream()
        .limit(breakLine)
        .map(s -> s.split("\\|"))
        .collect(Collectors.groupingBy(t -> Integer.parseInt(t[0]), Collectors.mapping(t -> Integer.parseInt(t[1]), Collectors.toSet())));
    
    var res = IntStream.range(breakLine + 1, lines.size())
        .mapToObj(lines::get)
        .map(s -> Arrays.stream(s.split(",")).map(n -> Integer.parseInt(n)).toList())
        .filter(line -> IntStream.range(1, line.size())
            .allMatch(i -> !dependencies.computeIfAbsent(line.get(i), _ -> new HashSet<>())
            .contains(line.get(i - 1))))
        .mapToInt(l -> l.get(l.size() / 2))
        .sum();
    
    System.out.println(res);
  }
  
}
