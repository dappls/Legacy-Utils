package net.dappls.legacy_utils.client.Honey;

import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Blocks;

import java.awt.Color;
import java.util.*;

public class HoneySolver {

    public static final int MAX_MOVES = 100000;

    // --- Wall blocks ---
    public static final List<Block> WALL_BLOCKS = Arrays.asList(
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.STRIPPED_ACACIA_LOG,
            Blocks.STRIPPED_ACACIA_WOOD
    );

    public enum CardinalDirections { NORTH, EAST, SOUTH, WEST }

    public static class MazeBlockPos {
        MazeBlockPos parent;
        CardinalDirections direction;
        BlockPos pos;
        int gCost; // distance from start
        int fCost; // total estimated cost

        public MazeBlockPos(MazeBlockPos parent, CardinalDirections direction, BlockPos pos, int gCost, int fCost) {
            this.parent = parent;
            this.direction = direction;
            this.pos = pos;
            this.gCost = gCost;
            this.fCost = fCost;
        }

        public BlockPos getPos() { return pos; }
    }

    // --- Path and trail ---
    public static final List<BlockPos> solvedPath = new ArrayList<>();
    public static boolean trailActive = false;

    // --- Wall check ---
    public static boolean isWall(BlockPos pos) {
        if (MinecraftClient.getInstance().world == null) return true;
        Block block = MinecraftClient.getInstance().world.getBlockState(pos).getBlock();
        return WALL_BLOCKS.contains(block);
    }

    // --- Heuristic: Manhattan distance ---
    private static int heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    // --- Get goal (active candle's pressure plate) ---
    private static BlockPos getGoalPos() {
        return CandleChecker.getActivePressurePlate();
    }

    // --- A* Solver ---
    public static void solveToCandle(BlockPos startPos) {
        if (MinecraftClient.getInstance().world == null) return;

        BlockPos goal = getGoalPos();
        if (goal == null) {
            ChatUtils.sendClientMessage("No lit candle found!");
            return;
        }

        PriorityQueue<MazeBlockPos> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<BlockPos> closedSet = new HashSet<>();

        MazeBlockPos startNode = new MazeBlockPos(null, null, startPos, 0, heuristic(startPos, goal));
        openSet.add(startNode);

        int moves = 0;
        ChatUtils.sendClientMessage("A* search started...");

        while (!openSet.isEmpty() && moves < MAX_MOVES) {
            MazeBlockPos current = openSet.poll();
            moves++;

            if (current.pos.equals(goal)) {
                buildPath(current);
                ChatUtils.sendClientMessage("A* solved in " + moves + " moves");
                trailActive = true;
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.closeScreen();
                }
                return;
            }

            closedSet.add(current.pos);

            for (CardinalDirections dir : CardinalDirections.values()) {
                BlockPos neighborPos = switch (dir) {
                    case NORTH -> current.pos.north();
                    case EAST -> current.pos.east();
                    case SOUTH -> current.pos.south();
                    case WEST -> current.pos.west();
                };

                if (isWall(neighborPos) || closedSet.contains(neighborPos))
                    continue;

                int gCost = current.gCost + 1;
                int fCost = gCost + heuristic(neighborPos, goal);
                MazeBlockPos neighbor = new MazeBlockPos(current, dir, neighborPos, gCost, fCost);

                // Only add if not in open set with lower cost
                boolean shouldAdd = openSet.stream()
                        .noneMatch(n -> n.pos.equals(neighborPos) && n.fCost <= fCost);

                if (shouldAdd) openSet.add(neighbor);
            }
        }

        ChatUtils.sendClientMessage("A* failed to find a path(try navigating back to the middle of the maze)");
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.closeScreen();
        }
    }

    // --- Build solved path ---
    private static void buildPath(MazeBlockPos goal) {
        solvedPath.clear();
        MazeBlockPos current = goal;
        while (current.parent != null && current.direction != null) {
            solvedPath.add(current.getPos());
            current = current.parent;
        }
        Collections.reverse(solvedPath);
    }

    // --- Particle trail ---
    public static void registerTrailListener() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!trailActive || MinecraftClient.getInstance().world == null || solvedPath.isEmpty()) return;

            for (int i = 0; i < solvedPath.size(); i++) {
                BlockPos pos = solvedPath.get(i);
                int color = lengthToColorInt(i, solvedPath.size());
                spawnParticle(pos, color);
            }
        });
    }

    private static void spawnParticle(BlockPos pos, int rgbColor) {
        Vec3d vec = Vec3d.ofCenter(pos);
        DustParticleEffect particle = new DustParticleEffect(rgbColor, (float) 1.0);
        MinecraftClient.getInstance().world.addParticle(particle, vec.x, vec.y + 0.1, vec.z, 0, 0.01, 0);
    }

    private static int lengthToColorInt(int index, int total) {
        float hue = (float) index / (float) total;
        return Color.HSBtoRGB(hue, 1.0f, 1.0f);
    }

}
