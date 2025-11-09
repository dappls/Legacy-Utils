package net.dappls.legacy_utils.client.Ingenuity;


import net.dappls.legacy_utils.client.Util.ChatUtils;

import java.util.*;

import static net.dappls.legacy_utils.client.Ingenuity.PuzzleListeners.*;

public class PuzzleSolver {

    private static final int MAX_STEPS = 20;

    private static final List<List<Integer>> INGENUITY_FINAL_VALUES = Arrays.asList(
            Arrays.asList(1, 5),
            Arrays.asList(3, 6),
            Arrays.asList(4, 4),
            Arrays.asList(3, 2),
            Arrays.asList(2, 2),
            Arrays.asList(3, 5),
            Arrays.asList(1, 6),
            Arrays.asList(3, 4)
    );

    private static boolean isPuzzleSolved(List<Integer> currentValues) {
        for (int i = 0; i < currentValues.size(); i++) {
            if (!INGENUITY_FINAL_VALUES.get(i).contains(currentValues.get(i))) return false;
        }
        return true;
    }

    static class Node {
        List<Integer> values;
        Node parent;
        String action;

        Node(List<Integer> values, Node parent, String action) {
            this.values = values;
            this.parent = parent;
            this.action = action;
        }
    }

    // Puzzle shift operations
    public static List<Integer> shiftLeftChamber(List<Integer> v) {
        List<Integer> n = new ArrayList<>(v);
        n.set(0, v.get(1));
        n.set(1, v.get(3));
        n.set(2, v.get(0));
        n.set(3, v.get(2));
        return n;
    }

    public static List<Integer> shiftMiddle(List<Integer> v) {
        List<Integer> n = new ArrayList<>(v);
        n.set(0, v.get(4));
        n.set(1, v.get(1));
        n.set(2, v.get(6));
        n.set(3, v.get(3));
        n.set(4, v.get(0));
        n.set(5, v.get(5));
        n.set(6, v.get(2));
        n.set(7, v.get(7));
        return n;
    }

    public static List<Integer> shiftRightChamber(List<Integer> v) {
        List<Integer> n = new ArrayList<>(v);
        n.set(4, v.get(7));
        n.set(5, v.get(4));
        n.set(6, v.get(5));
        n.set(7, v.get(6));
        return n;
    }

    public static List<String> solvePuzzle(List<Integer> startValues) {
        Queue<Node> queue = new LinkedList<>();
        Set<List<Integer>> visited = new HashSet<>();

        Node root = new Node(startValues, null, null);
        queue.add(root);
        visited.add(startValues);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (isPuzzleSolved(current.values)) {
                List<String> path = reconstructPath(current);

                PuzzleListeners.loadSolution(path);
                switch (path.getFirst()) {
                    case "shiftLeftChamber" ->
                            RenderPuzzle.pos = LEFT_BUTTON_POS;
                    case "shiftRightChamber" -> RenderPuzzle.pos = RIGHT_BUTTON_POS;
                    case "shiftMiddle" -> RenderPuzzle.pos = LECTERN_POS;
                    case null -> RenderPuzzle.pos = null;
                    default -> throw new IllegalStateException("Unexpected value: " + path.getFirst());
                }
                return path;
            }

            // Try all 3 actions
            List<Integer> afterLeft = shiftLeftChamber(current.values);
            if (visited.add(afterLeft))
                queue.add(new Node(afterLeft, current, "shiftLeftChamber"));

            List<Integer> afterMid = shiftMiddle(current.values);
            if (visited.add(afterMid))
                queue.add(new Node(afterMid, current, "shiftMiddle"));

            List<Integer> afterRight = shiftRightChamber(current.values);
            if (visited.add(afterRight))
                queue.add(new Node(afterRight, current, "shiftRightChamber"));

            if (reconstructPath(current).size() > MAX_STEPS) {
                ChatUtils.sendClientMessage("Cannot solve puzzle within " + MAX_STEPS + " steps.");
                return null;
            }
        }

        ChatUtils.sendClientMessage("No solution found.");
        return null;
    }

    private static List<String> reconstructPath(Node goalNode) {
        List<String> path = new ArrayList<>();
        Node current = goalNode;
        while (current.parent != null) {
            path.add(current.action);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
