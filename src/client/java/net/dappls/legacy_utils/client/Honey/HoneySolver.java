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

import java.awt.*;
import java.util.*;
import java.util.List;

import static net.dappls.legacy_utils.client.Util.TrailRenderer.renderCube;

public class HoneySolver {


    public static final List<BlockPos> solvedPath = new ArrayList<>();
    public static boolean trailActive = false;
    public static boolean honeyActive = false;
    public static final int MAX_MOVES = 100000;
    public static BlockPos currentGoal = null;
    public static final List<Block> WALL_BLOCKS = Arrays.asList(
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.STRIPPED_ACACIA_LOG,
            Blocks.STRIPPED_ACACIA_WOOD
    );
    public static final BlockPos finalPressurePlate = new BlockPos(9777, 55, 52329);
    public enum CardinalDirections { NORTH, EAST, SOUTH, WEST }

    public static void disableHoney() {
        honeyActive = false;
        trailActive = false;
        solvedPath.clear();
        currentGoal = null;
    }


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

    // --- Wall check ---
    public static boolean isWall(BlockPos pos) {
        if (MinecraftClient.getInstance().world == null) return true;
        Block block = MinecraftClient.getInstance().world.getBlockState(pos).getBlock();
        return WALL_BLOCKS.contains(block);
    }






    private static int heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    public static void solveAStar(BlockPos startPos, BlockPos goal) {
        if (MinecraftClient.getInstance().player != null && !MinecraftClient.getInstance().player.getBlockPos().isWithinDistance(new BlockPos(9763, 55, 52356), 100)) {
            disableHoney();
            return;
        }
        if (MinecraftClient.getInstance().world == null || goal == null) return;

        PriorityQueue<MazeBlockPos> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<BlockPos> closedSet = new HashSet<>();

        MazeBlockPos startNode = new MazeBlockPos(null, null, startPos, 0, heuristic(startPos, goal));
        openSet.add(startNode);

        int moves = 0;

        while (!openSet.isEmpty() && moves < MAX_MOVES && honeyActive) {
            MazeBlockPos current = openSet.poll();
            moves++;

            if (current.pos.equals(goal)) {
                buildPath(current);
                trailActive = true;
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

        ChatUtils.sendClientMessage("Failed to find path ");
    }

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
        // Tick logic
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null || !honeyActive) return;
            if (MinecraftClient.getInstance().player != null) {
                BlockPos playerPos = new BlockPos(MinecraftClient.getInstance().player.getBlockPos().getX(), 55, MinecraftClient.getInstance().player.getBlockPos().getZ());

                List<BlockPos> ActivePlates = CandleChecker.getActivePressurePlates();

                if (ActivePlates.isEmpty()) {
                    solvedPath.clear();
                } else {
                    currentGoal = ActivePlates.getFirst();
                    solveAStar(playerPos, currentGoal);
                    return;
                }

                if (MinecraftClient.getInstance().world != null && !MinecraftClient.getInstance().world.getBlockState(finalPressurePlate).isOf(Blocks.BAMBOO_PRESSURE_PLATE)) {
                    solveAStar(playerPos, finalPressurePlate);
                }
            }
        });





        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (!trailActive || MinecraftClient.getInstance().world == null || solvedPath.isEmpty()) return;
            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null || solvedPath.isEmpty()) return;
            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());
            for (BlockPos pos : solvedPath) {

                renderCube(matrices, buffer, camera, pos, 5,80,220);
            }
        });
    }
}