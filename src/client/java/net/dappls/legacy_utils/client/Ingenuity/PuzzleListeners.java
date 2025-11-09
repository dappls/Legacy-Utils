package net.dappls.legacy_utils.client.Ingenuity;

import net.dappls.legacy_utils.client.GUI.IngenuityMenu;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PuzzleListeners {


    public static final BlockPos LEFT_BUTTON_POS = new BlockPos(-12204, 72, 12579);
    public static final BlockPos RIGHT_BUTTON_POS = new BlockPos(-12156, 72, 12579);
    public static final BlockPos LECTERN_POS = new BlockPos(-12180, 72, 12579);
    private static final BlockPos LECTERN_OBSERVER_POS = LECTERN_POS.down();


    private static boolean lastLeftButtonPowered = false;
    private static boolean lastRightButtonPowered = false;
    private static boolean lastLecternObserverPowered = false;


    private static final Queue<String> solutionQueue = new LinkedList<>();

    public static void register() {


        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world == null || !world.isClient) return ActionResult.PASS;
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            String action = null;


            if (state.isOf(Blocks.STONE_BUTTON) || state.isOf(Blocks.POLISHED_BLACKSTONE_BUTTON)) {
                if (pos.equals(LEFT_BUTTON_POS)) action = "shiftLeftChamber";
                else if (pos.equals(RIGHT_BUTTON_POS)) action = "shiftRightChamber";
            }

            if (action != null) {
                onAction(action);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            checkButtonPower(client, LEFT_BUTTON_POS, "shiftLeftChamber");
            checkButtonPower(client, RIGHT_BUTTON_POS, "shiftRightChamber");
            checkLecternObserver(client);
        });
    }


    private static void checkButtonPower(MinecraftClient client, BlockPos pos, String action) {
        BlockState state = client.world.getBlockState(pos);
        if (!state.contains(Properties.POWERED)) return;
        boolean powered = state.get(Properties.POWERED);
        boolean wasPowered = switch (action) {
            case "shiftLeftChamber" -> lastLeftButtonPowered;
            case "shiftRightChamber" -> lastRightButtonPowered;
            default -> false;
        };

        if (powered && !wasPowered) {
            onAction(action);
        }

        // Update stored state
        switch (action) {
            case "shiftLeftChamber" -> lastLeftButtonPowered = powered;
            case "shiftRightChamber" -> lastRightButtonPowered = powered;
        }

    }


    private static void checkLecternObserver(MinecraftClient client) {
        if (client.world != null) {

            if (!(client.world.getBlockState(LECTERN_OBSERVER_POS).isOf(Blocks.OBSERVER))) {
                return;
            }
            if (!client.world.getBlockState(LECTERN_OBSERVER_POS).contains(Properties.POWERED)) {
                return;
            }

            boolean powered = client.world.getBlockState(LECTERN_OBSERVER_POS).get(Properties.POWERED);
            if (powered && !lastLecternObserverPowered) {
                onAction("shiftMiddle");
            }
            lastLecternObserverPowered = powered;
        }


    }

    // --- Chat feedback ---
    private static void onAction(String action) {

        // Show the next step in the puzzle
        if (solutionQueue.isEmpty()) {
            return;
        }

        String next = solutionQueue.poll();
        if (action.equals(next)) {
            next = solutionQueue.peek();
            if(IngenuityMenu.chatEnabled) {
                ChatUtils.sendClientMessage("Next step: §b" + next);
            }
            switch (next) {
                case "shiftLeftChamber" ->
                        RenderPuzzle.pos = LEFT_BUTTON_POS;
                case "shiftRightChamber" -> RenderPuzzle.pos = RIGHT_BUTTON_POS;
                case "shiftMiddle" -> RenderPuzzle.pos = LECTERN_POS;
                case null -> RenderPuzzle.pos = null;
                default -> throw new IllegalStateException("Unexpected value: " + next);
            }
        } else {
            solutionQueue.clear();
            RenderPuzzle.pos = null;

        }

    }

    public static void loadSolution(List<String> solution) {
        solutionQueue.clear();
        if (solution != null && !solution.isEmpty()) {
            solutionQueue.addAll(solution);
            if(IngenuityMenu.chatEnabled) {
                ChatUtils.sendClientMessage("First step: §b" + solutionQueue.peek());
            }
        }
    }
}
