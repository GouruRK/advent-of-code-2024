package fr.aoc.day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AdventOfCode08P2 {

  private static final String EXAMPLE = """
          T.........
          ...T......
          .T........
          ..........
          ..........
          ..........
          ..........
          ..........
          ..........
          ..........
          """;
  
  record Antena(int x, int y) {
  }

  public static void solve(List<Antena> antenas, Set<Antena> allAntenas, int width, int height) {
    int dx, dy;
    Antena newAntena;
    for (int i = 0; i < antenas.size(); i++) {
      for (int j = 0; j < antenas.size(); j++) {
        if (i == j) {
          continue;
        }

        dx = antenas.get(i).x - antenas.get(j).x;
        dy = antenas.get(i).y - antenas.get(j).y;
        newAntena = new Antena(antenas.get(i).x + dx, antenas.get(i).y + dy);
        
        while (newAntena.x >= 0 && newAntena.x < width && newAntena.y >= 0 && newAntena.y < height) {
          allAntenas.add(newAntena);
          newAntena = new Antena(newAntena.x + dx, newAntena.y + dy);
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    List<String> lines;

    if (args.length == 0) {
      lines = EXAMPLE.strip().lines().toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode08P2.java [FILE ?]");
      return;
    }
    
    int width = lines.getFirst().length();
    int height = lines.size();

    var r = IntStream.range(0, height).mapToObj(i -> i)
        .flatMap(
            y -> IntStream.range(0, width)
            .filter(x -> lines.get(y).charAt(x) != '.')
            .mapToObj(x -> new Antena(x, y)))
        .collect(Collectors.groupingBy(antena -> lines.get(antena.y).charAt(antena.x)));

    var s = r.values().stream().flatMap(l -> l.stream()).collect(Collectors.toSet());
    r.values().stream().forEach(a -> solve(a, s, width, height));

    System.err.println(s.size());
  }

}
