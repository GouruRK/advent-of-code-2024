package fr.aoc.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public final class AdventOfCode03P2 {

  private static final String EXAMPLE = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))";
  
  public static void main(String[] args) throws IOException {
    String line;
    
    if (args.length == 0) {
      line = EXAMPLE;
    } else if (args.length == 1) {
      line = Files.lines(Path.of(args[0])).findFirst().get();
    } else {
      System.err.println("Usage: java AdventOfCode03P2.java [FILE ?]");
      return;
    }
    var globalPattern = Pattern.compile("mul\\((\\d+)\\,(\\d+)\\)|don't|do");
    var mulPattern = Pattern.compile("mul\\((\\d+)\\,(\\d+)\\)");
    var matcher = globalPattern.matcher(line);
    List<String> array = matcher.results().map(r -> r.group(0)).toList();
    
    boolean disable = false;
    int res = 0;
    for (var t: array) {
      switch (t) {
        case "don't" -> disable = true;
        case "do" -> disable = false;
        default  -> {
          if (!disable) {
            var results = mulPattern.matcher(t).results();
            res += results.mapToInt(r -> Integer.parseInt(r.group(1))*Integer.parseInt(r.group(2))).sum();
          }
        }
      }
    }
    System.out.println(res);
  }
  
}
