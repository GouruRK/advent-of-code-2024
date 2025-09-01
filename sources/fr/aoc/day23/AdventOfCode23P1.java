package fr.aoc.day23;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public final class AdventOfCode23P1 {
  private static final String EXAMPLE = """
      kh-tc
      qp-kh
      de-cg
      ka-co
      yn-aq
      qp-ub
      cg-tb
      vc-aq
      tb-ka
      wh-tc
      yn-cg
      kh-ub
      ta-co
      de-co
      tc-td
      tb-wq
      wh-td
      ta-ka
      td-qp
      aq-cg
      wq-ub
      ub-vc
      de-ta
      wq-aq
      wq-vc
      wh-yn
      ka-de
      kh-ta
      co-tc
      wh-qp
      tb-vc
      td-yn
      """;

  public static void main(String[] args) throws IOException {
    Stream<String> lines;

    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n"));
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode23P1.java [FILE ?]");
      return;
    }

    var graph = new HashMap<String, HashSet<String>>();
    lines.map(line -> line.split("-")).forEach(r -> {
      graph.computeIfAbsent(r[0], _ -> new HashSet<>()).add(r[1]);
      graph.computeIfAbsent(r[1], _ -> new HashSet<>()).add(r[0]);
    });

    int acc = 0;
    
    for (var u : graph.keySet()) {
      for (String v : graph.get(u)) {
        if (v.compareTo(u) <= 0) {
          continue;
        }
        for (String w : graph.get(v)) {
          if (w.compareTo(v) <= 0) {
            continue;
          }
          if (graph.get(u).contains(w) && (u.charAt(0) == 't' || v.charAt(0) == 't' || w.charAt(0) == 't')) {
            acc++;
          }
        }
      }
    }
    System.out.println(acc);
  }
}
