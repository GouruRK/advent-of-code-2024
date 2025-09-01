package fr.aoc.day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AdventOfCode09P2 {

  private static final String EXAMPLE = "2333133121414131402";
  
  record Indexes(int l, int r) implements Comparable<Indexes> {

    @Override
    public int compareTo(Indexes indexes) {
      return l - indexes.l;
    }
    
    int length() {
      return r - l;
    }
   }
  
  private static List<Indexes> groups(int[] array, IntPredicate predicate) {
    var res = new ArrayList<Indexes>();
    var start = 0;
    
    for (int i = 1; i < array.length; i++) {
      if (array[start] != array[i]) {
        if (predicate.test(array[start])) {
          res.add(new Indexes(start, i));
        }
        start = i;
      }
    }
    if (predicate.test(array[start])) {
      res.add(new Indexes(start, array.length));
    }
    return res;
  }
  
  public static void main(String[] args) throws IOException {
    String line;

    if (args.length == 0) {
      line = EXAMPLE;
    } else if (args.length == 1) {
      line = Files.lines(Path.of(args[0])).toList().getFirst();
    } else {
      System.err.println("Usage: java AdventOfCode09P2.java [FILE ?]");
      return;
    }
    
    var array = IntStream.range(0, line.length())
        .flatMap(i -> IntStream.range(0, line.charAt(i) - '0')
            .map(_ -> i % 2 == 0 ? i / 2 : -1)
        ).toArray();
    var gaps = groups(array, i -> i == -1).stream()
      .collect(
          Collectors.groupingBy(Indexes::length,
                                Collectors.mapping(Function.identity(),
                                                   Collectors.toCollection(PriorityQueue<Indexes>::new))));
    var fragments = groups(array, i -> i != -1).reversed();
    var maxEmptySpace = gaps.keySet().stream().mapToInt(x -> x).max().getAsInt();
    
    for (var fragment: fragments) {
      if (fragment.length() > maxEmptySpace) {
        continue;
      }
      var possibleGaps = gaps.entrySet().stream()
          .filter(entry -> entry.getKey() >= fragment.length())
          .filter(entry -> !entry.getValue().isEmpty())
          .min(Map.Entry.comparingByValue(Comparator.comparing(PriorityQueue::peek)));
      
      possibleGaps.ifPresent(entry -> {
        var gap = entry.getValue().remove();
        IntStream.range(gap.l, gap.l + fragment.length()).forEach(i -> array[i] = array[fragment.l]);
        IntStream.range(fragment.l, fragment.r).forEach(i -> array[i] = -1);
        gaps.computeIfAbsent(gap.length() - fragment.length(), _ -> new PriorityQueue<>()).add(new Indexes(gap.l + fragment.length(), gap.r));
      });
    }
    // System.out.println(Arrays.stream(array).mapToObj(i -> i == -1 ? ".": "" + i).collect(Collectors.joining()));
    System.out.println(IntStream.range(0, array.length).filter(i -> array[i] != -1).mapToLong(i -> i*array[i]).sum());
  }
  
}  
