package fr.aoc.day24;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

public final class AdventOfCode24P1 {
  private static final String EXAMPLE = """
      x00: 1
      x01: 1
      x02: 1
      y00: 0
      y01: 1
      y02: 0
      
      x00 AND y00 -> z00
      x01 XOR y01 -> z01
      x02 OR y02 -> z02
      """;

  private static final String EXAMPLE2 = """
      x00: 1
      x01: 0
      x02: 1
      x03: 1
      x04: 0
      y00: 1
      y01: 1
      y02: 1
      y03: 1
      y04: 1
      
      ntg XOR fgs -> mjb
      y02 OR x01 -> tnw
      kwq OR kpj -> z05
      x00 OR x03 -> fst
      tgd XOR rvg -> z01
      vdt OR tnw -> bfw
      bfw AND frj -> z10
      ffh OR nrd -> bqk
      y00 AND y03 -> djm
      y03 OR y00 -> psh
      bqk OR frj -> z08
      tnw OR fst -> frj
      gnj AND tgd -> z11
      bfw XOR mjb -> z00
      x03 OR x00 -> vdt
      gnj AND wpb -> z02
      x04 AND y00 -> kjc
      djm OR pbm -> qhw
      nrd AND vdt -> hwm
      kjc AND fst -> rvg
      y04 OR y02 -> fgs
      y01 AND x02 -> pbm
      ntg OR kjc -> kwq
      psh XOR fgs -> tgd
      qhw XOR tgd -> z09
      pbm OR djm -> kpj
      x03 XOR y03 -> ffh
      x00 XOR y04 -> ntg
      bfw OR bqk -> z06
      nrd XOR fgs -> wpb
      frj XOR qhw -> z04
      bqk OR frj -> z07
      y03 OR x01 -> nrd
      hwm AND bqk -> z03
      tgd XOR rvg -> z12
      tnw OR pbm -> gnj
      """;
  
  private static final Map<String, IntBinaryOperator> EQUIV = Map.of(
      "AND", (l, r) -> l & r,
      "OR", (l, r) -> l | r,
      "XOR", (l, r) -> l ^ r);
  
  record Operation(String left, IntBinaryOperator f, String right, String output) {
    int compute(int left, int right) {
      return f.applyAsInt(left, right);
    }
    
    public static Operation fromString(String input) {
      var r = input.split(" ");
      return new Operation(r[0], EQUIV.get(r[1]), r[2], r[4]);
    }
    
  }
  
  public static void resolve(HashMap<String, Integer> known, HashMap<String, ArrayList<Operation>> waiting, Operation op, int left, int right) {
    known.put(op.output, op.compute(left, right));
    
    var remaining = waiting.remove(op.output);
    if (remaining == null) {
      return;
    }
    
    for (var otherOp: remaining) {
      var l = known.get(otherOp.left);
      var r = known.get(otherOp.right);
      
      if (l != null && r != null) {
        resolve(known, waiting, otherOp, l, r);
      }
    }
  }
  
  public static void main(String[] args) throws IOException {
    List<String> lines;

    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE2.strip().split("\n")).toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode24P1.java [FILE ?]");
      return;
    }
    
    int breakLine = lines.indexOf("");
    HashMap<String, Integer> knownValues = new HashMap<>();
    HashMap<String, ArrayList<Operation>> waitingForValues = new HashMap<>();
    
    lines.subList(0, breakLine).stream()
      .map(line -> line.split(" "))
      .forEach(r -> knownValues.put(r[0].substring(0, r[0].length() - 1), Integer.parseInt(r[1])));
    
    lines.subList(breakLine + 1, lines.size()).stream()
      .map(Operation::fromString)
      .forEach(op -> {
        var left = knownValues.get(op.left);
        var right = knownValues.get(op.right);
        
        if (left != null && right != null) {
          resolve(knownValues, waitingForValues, op, left, right);
        } else {
          waitingForValues.computeIfAbsent(op.left, _ -> new ArrayList<>()).add(op);
          waitingForValues.computeIfAbsent(op.right, _ -> new ArrayList<>()).add(op);
        }
      });
    
    var output = knownValues.entrySet().stream()
      .filter(entry -> entry.getKey().charAt(0) == 'z')
      .sorted(Comparator.comparing(Entry::getKey))
      .map(Entry::getValue).toList();
      
    System.out.println(IntStream.range(0, output.size()).mapToLong(i -> ((long)output.get(i)) << i).sum());
  }
}
