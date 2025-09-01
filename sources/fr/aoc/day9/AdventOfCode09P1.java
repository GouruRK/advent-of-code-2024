package fr.aoc.day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

public final class AdventOfCode09P1 {

  private static final String EXAMPLE = "2333133121414131402";
  
  public static void main(String[] args) throws IOException {
    String line;

    if (args.length == 0) {
      line = EXAMPLE;
    } else if (args.length == 1) {
      line = Files.lines(Path.of(args[0])).toList().getFirst();
    } else {
      System.err.println("Usage: java AdventOfCode09P1.java [FILE ?]");
      return;
    }
    
    var r = IntStream.range(0, line.length()).flatMap(i -> IntStream.range(0, line.charAt(i) - '0').map(x -> i % 2 == 0 ? i / 2 : -1)).toArray();
    
    int leftIndex = 0, rightIndex = r.length - 1;
    while (leftIndex < rightIndex) {
      if (r[leftIndex] != -1) {
        leftIndex++;
        continue;
      }
      if (r[rightIndex] == -1) {
        r[rightIndex--] = 0;
        continue;
      }
      
      r[leftIndex++] = r[rightIndex];
      r[rightIndex--] = 0;
    }
    
    System.out.println(IntStream.range(0, leftIndex + 1).mapToLong(i -> i*r[i]).sum());
  }
  
}  
