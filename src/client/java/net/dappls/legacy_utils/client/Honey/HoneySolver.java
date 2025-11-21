package net.dappls.legacy_utils.client.Honey;

import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;

import java.awt.Color;
import java.util.*;

import static net.dappls.legacy_utils.client.Util.TrailRenderer.renderCube;

public class HoneySolver {
    public static boolean honeyActive = false;
    public static final int MAX_MOVES = 100000;
    public static boolean returning = false;
    public static final List<BlockPos> solvedPathOriginal = new ArrayList<>();
    // Queue of candle pressure plates to visit
    public static final Queue<BlockPos> candleGoals = new LinkedList<>();

    public static BlockPos currentGoal = null;
    public static boolean solving = false;  // fully controls solver loop

    public static void disableHoney() {
        honeyActive = false;
        trailActive = false;
        solvedPath.clear();
        solving = false;
        returning = false;
        candleGoals.clear();
        solvedPathOriginal.clear();
        currentGoal = null;
        ChatUtils.sendClientMessage("Honey solver disabled.");
    }

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

    private static void loadCandleQueue() {
        candleGoals.clear();
        List<BlockPos> powered = CandleChecker.getActivePressurePlates();

        if (powered.isEmpty()) {
            // keep queue empty
            return;
        }

        candleGoals.addAll(powered);
    }

    /**
     * Public entry: start the whole solve queue from player's current pos.
     * Caller should ensure player != null.
     */
    public static void startSolving(BlockPos startPos) {
        if (solving || !honeyActive) return;

        loadCandleQueue();

        if (candleGoals.isEmpty()) {
            ChatUtils.sendClientMessage("No candles to solve!");
            return;
        }

        solving = true;
        ChatUtils.sendClientMessage("Honey solver started. Lit Candles: " + candleGoals.size());
        solveNextCandle(startPos);
    }

    private static void solveNextCandle(BlockPos startPos) {
        if (!honeyActive) {
            solving = false;
            return;
        }

        // reload just in case
        if (candleGoals.isEmpty()) {
            loadCandleQueue();
        }

        if (candleGoals.isEmpty()) {
            solving = false;
            ChatUtils.sendClientMessage("All candles solved!");
            return;
        }

        currentGoal = candleGoals.poll();
        returning = false;
        solvedPath.clear();
        solvedPathOriginal.clear();

        solveAStar(startPos, currentGoal);
    }

    // --- Heuristic: Manhattan distance ---
    private static int heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    public static void solveAStar(BlockPos startPos, BlockPos goal) {
        if (MinecraftClient.getInstance().world == null || goal == null) return;

        PriorityQueue<MazeBlockPos> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<BlockPos> closedSet = new HashSet<>();

        MazeBlockPos startNode = new MazeBlockPos(null, null, startPos, 0, heuristic(startPos, goal));
        openSet.add(startNode);

        int moves = 0;

        while (!openSet.isEmpty() && moves < MAX_MOVES && honeyActive && solving) {
            MazeBlockPos current = openSet.poll();
            moves++;

            if (current.pos.equals(goal)) {
                buildPath(current);
                trailActive = true;
                ChatUtils.sendClientMessage("Path built to candle");
                return;
            }

            closedSet.add(current.pos);

            for (CardinalDirections dir : CardinalDirections.values()) {
                BlockPos neighborPos = switch (dir) {
                    case NORTH -> current.pos.north();
                    case EAST  -> current.pos.east();
                    case SOUTH -> current.pos.south();
                    case WEST  -> current.pos.west();
                };

                if (isWall(neighborPos) || closedSet.contains(neighborPos))
                    continue;

                int gCost = current.gCost + 1;
                int fCost = gCost + heuristic(neighborPos, goal);

                MazeBlockPos neighbor = new MazeBlockPos(current, dir, neighborPos, gCost, fCost);

                boolean shouldAdd = openSet.stream()
                        .noneMatch(n -> n.pos.equals(neighborPos) && n.fCost <= fCost);

                if (shouldAdd) openSet.add(neighbor);
            }
        }

        ChatUtils.sendClientMessage("Failed for candle at " + goal);
    }

    private static void buildPath(MazeBlockPos goal) {
        solvedPath.clear();
        solvedPathOriginal.clear();

        MazeBlockPos current = goal;
        while (current.parent != null && current.direction != null) {
            solvedPath.add(current.getPos());
            current = current.parent;
        }
        Collections.reverse(solvedPath);

        // Save original
        solvedPathOriginal.addAll(solvedPath);
    }

    // --- Particle trail ---
    public static void registerTrailListener() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (!trailActive || MinecraftClient.getInstance().world == null || solvedPath.isEmpty()) return;
            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null || solvedPath.isEmpty()) return;
            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());
            for (int i = 0; i < solvedPath.size(); i++) {
                BlockPos pos = solvedPath.get(i);
                int total = solvedPath.size();
                float hue = (float) i / total;
                Color color = Color.getHSBColor(hue, 1f, 1f);
                renderCube(
                        matrices, buffer, camera, pos,
                        color.getRed() / 255f,
                        color.getGreen() / 255f,
                        color.getBlue() / 255f
                );
            }
        });

        // Register tick listener once (idempotent if called multiple times)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Only process when solver is active
            if (!honeyActive || !solving) return;
            removeNearestTrailBlock();
        });
    }

    public static void removeNearestTrailBlock() {
        if (MinecraftClient.getInstance().player == null || solvedPath.isEmpty()) return;
        if (!honeyActive || !solving) return;

        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();

        int touchedIndex = -1;
        for (int i = 0; i < solvedPath.size(); i++) {
            if (playerPos.isWithinDistance(solvedPath.get(i), 1.0)) {
                touchedIndex = i;
                break;
            }
        }

        if (touchedIndex >= 0) {
            // Remove "queue style"
            solvedPath.subList(0, touchedIndex + 1).clear();

            // Forward path finished: build return trail
            if (solvedPath.isEmpty() && !returning) {
                returning = true;

                // reverse original saved forward path and use that as return trail
                Collections.reverse(solvedPathOriginal);
                solvedPath.addAll(solvedPathOriginal);

                ChatUtils.sendClientMessage("Forward path cleared — returning to center.");
                return;
            }

            // Return path finished → start next goal
            if (solvedPath.isEmpty() && returning) {
                trailActive = false;
                returning = false;

                // When returned to center, refresh queue (candles may have changed),
                // and start the next candle if any remain.
                loadCandleQueue();

                if (candleGoals.isEmpty()) {
                    solving = false;
                    honeyActive = false; // optionally auto-disable
                    ChatUtils.sendClientMessage("All candles solved!");
                } else if (solving && honeyActive) {
                    // Use player's current pos as start for the next path
                    BlockPos start = MinecraftClient.getInstance().player.getBlockPos();
                    solveNextCandle(start);
                }
            }
        }
    }
}
