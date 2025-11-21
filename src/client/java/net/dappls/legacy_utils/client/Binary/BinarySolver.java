package net.dappls.legacy_utils.client.Binary;

import net.dappls.legacy_utils.client.Util.TrailRenderer;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class BinarySolver {

    // --- Tables for puzzle logic ---
    private static final Map<List<Integer>, List<Boolean>> tableOn = new HashMap<>();
    private static final Map<List<Integer>, List<Boolean>> tableOff = new HashMap<>();
    //facing -Z
    // --- Puzzle blocks ---
    private static final BlockPos LEVER_A = new BlockPos(10044, 64, 49996);
    private static final BlockPos LEVER_B = new BlockPos(10046, 64, 49996);
    private static final BlockPos LEVER_C = new BlockPos(10046, 62, 49996);
    private static final BlockPos BUTTON_POS = new BlockPos(10044, 62, 49996);
    public static final BlockPos LAMP_POS = new BlockPos(10049, 61, 49998);

    // --- Runtime state ---
    public static boolean binaryActive = false;
    public static final List<BlockPos> errorLevers = new ArrayList<>();
    public static final List<List<Boolean>> expectedQueue = new ArrayList<>();
    public static int queueIndex = 0;

    static {
        // ON table
        tableOn.put(Arrays.asList(0, 0), Arrays.asList(true, true, true));
        tableOn.put(Arrays.asList(0, 1), Arrays.asList(false, false, false));
        tableOn.put(Arrays.asList(1, 0), Arrays.asList(false, true, true));
        tableOn.put(Arrays.asList(1, 1), Arrays.asList(true, false, false));

        // OFF table
        tableOff.put(Arrays.asList(0, 0), Arrays.asList(false, false, false));
        tableOff.put(Arrays.asList(0, 1), Arrays.asList(true, true, false));
        tableOff.put(Arrays.asList(1, 0), Arrays.asList(false, true, true));
        tableOff.put(Arrays.asList(1, 1), Arrays.asList(true, false, true));
    }

    // --- Public API ---

    /** Start the binary puzzle and build the expected queue */
    public static void startBinarySolve(List<Integer> desiredState) {
        if (binaryActive) return;

        binaryActive = true;
        expectedQueue.clear();
        queueIndex = 0;

        World world = MinecraftClient.getInstance().world;
        if (world == null) return;

        boolean lampState = getLampState(world);

        for (int i = 0; i < 4; i++) {
            int lampID = 2 * (3 - i);
            List<Integer> lampPair = Arrays.asList(
                    desiredState.get(lampID),
                    desiredState.get(lampID + 1)
            );
            List<Boolean> expected = lampState ? tableOn.get(lampPair) : tableOff.get(lampPair);
            expectedQueue.add(expected);
            lampState = (lampPair.getFirst() == 1);
        }

        ChatUtils.sendClientMessage("Binary Solver Started — Configure the first lever set.");
    }

    /** Register both render and tick listeners */
    public static void registerListeners() {
        // Render cubes above incorrect levers
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (!binaryActive || errorLevers.isEmpty()) return;

            World world = MinecraftClient.getInstance().world;
            if (world == null) return;

            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;

            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());

            for (BlockPos leverPos : errorLevers) {
                TrailRenderer.renderCube(matrices, buffer, camera, leverPos, 1.0f, 0.2f, 0.2f);
            }
        });

        // Tick listener to update errorLevers every tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (binaryActive) {
                updateErrorLevers();
            }
        });
    }

    /** Updates error levers based on current expected step */
    public static void updateErrorLevers() {
        World world = MinecraftClient.getInstance().world;
        if (world == null || expectedQueue.isEmpty() || queueIndex >= expectedQueue.size()) return;

        List<Boolean> expected = expectedQueue.get(queueIndex);

        boolean leverA = world.getBlockState(LEVER_A).get(Properties.POWERED);
        boolean leverB = world.getBlockState(LEVER_B).get(Properties.POWERED);
        boolean leverC = world.getBlockState(LEVER_C).get(Properties.POWERED);

        boolean correctA = leverA == expected.get(0);
        boolean correctB = leverB == expected.get(1);
        boolean correctC = leverC == expected.get(2);

        // Update errorLevers list for rendering
        errorLevers.clear();
        if (!correctA) errorLevers.add(LEVER_A);
        if (!correctB) errorLevers.add(LEVER_B);
        if (!correctC) errorLevers.add(LEVER_C);

        // Move to next step if correct and button pressed
        if (correctA && correctB && correctC && world.getBlockState(BUTTON_POS).get(Properties.POWERED)) {
            queueIndex++;
            if (queueIndex >= expectedQueue.size()) {
                binaryActive = false;
                errorLevers.clear();
                ChatUtils.sendClientMessage("Binary Puzzle Solved!");
            } else {
                ChatUtils.sendClientMessage("Step completed! Configure next lever set.");
            }
        }
    }

    // --- Helpers ---

    private static boolean getLampState(World world) {
        return world.getBlockState(BinarySolver.LAMP_POS).isOf(net.minecraft.block.Blocks.REDSTONE_LAMP) &&
                world.getBlockState(BinarySolver.LAMP_POS).get(net.minecraft.block.RedstoneLampBlock.LIT);
    }

    public static String formatStates(List<Boolean> states) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < states.size(); i++) {
            sb.append(states.get(i) ? "on" : "off");
            if (i < states.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
