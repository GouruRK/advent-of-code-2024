package fr.aoc.day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class AdventOfCode19P1 {
    
  private static final String EXAMPLE = """
            r, wr, b, g, bwu, rb, gb, br

            brwrr
            bggr
            gbbr
            rrbgbr
            ubwu
            bwurrg
            brgr
            bbrgwb
            """;
    
    public static void main(String[] args) throws IOException {
      List<String> lines;
  
      if (args.length == 0) {
        lines = EXAMPLE.strip().lines().toList();
      } else if (args.length == 1) {
        lines = Files.lines(Path.of(args[0])).toList();
      } else {
        System.err.println("Usage: java AdventOfCode19P1.java [FILE ?]");
        return;
      }
      
      var pattern = Pattern.compile(Arrays.stream(lines.getFirst().split(","))
          .map(p -> p.charAt(0) == ' ' ? p.subSequence(1, p.length()): p)
          .collect(Collectors.joining("|", "^(", ")+$")));
      System.out.println(lines.stream().skip(2).mapToInt(line -> pattern.matcher(line).matches() ? 1 : 0).sum());
    }
}

