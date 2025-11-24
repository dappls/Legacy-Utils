package net.dappls.legacy_utils.Toggles;

import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.*;
import java.util.List;

import static net.dappls.legacy_utils.client.Util.TrailRenderer.renderCube;

@Environment(EnvType.CLIENT)
public class WaterParticleTrail {

    private static boolean trailEnabled = false;
    private static final Map<String, List<BlockPos>> TRAILS = new HashMap<>();
    private static List<BlockPos> activeTrail = Collections.emptyList();
    private static boolean hasenteredbossroom = false;
    private static boolean hasreturnedfromconduit1 = false;
    private static boolean hasreturnedfromconduit2 = false;
    private static boolean hasreturnedfromconduit3 = false;
    private static boolean hasreturnedfromconduit4 = false;
    private static final BlockPos conduit1 = new BlockPos(-12073, 52, 12476);
    private static final BlockPos conduit2 = new BlockPos(-12048, 52, 12501);
    private static final BlockPos conduit3 = new BlockPos(-12023, 52, 12476);
    private static final BlockPos conduit4 = new BlockPos(-12048, 52, 12451);


    private static final BlockPos SPAWNER_POS = new BlockPos(-12048, 49, 12476);
    private static final BlockPos BOSS_ROOM_POS = new BlockPos(-12069, 37, 12476);

    public static boolean IsWithinSpawner(MinecraftClient client) {
        assert client.player != null;
        BlockPos playerPos = client.player.getBlockPos();

        boolean withinSpawner = Math.abs(playerPos.getX() - SPAWNER_POS.getX()) <= 8
                && Math.abs(playerPos.getZ() - SPAWNER_POS.getZ()) <= 8
                && Math.abs(playerPos.getY() - SPAWNER_POS.getY()) <= 100;
        boolean withinBossRoom = playerPos.isWithinDistance(BOSS_ROOM_POS, 3);


        return withinSpawner || withinBossRoom;
    }



    private static void init() {
        TRAILS.clear();
        TrailFileLoader.SetPath("WaterTrails/");

        TRAILS.put("pickaxe", new ArrayList<>(TrailFileLoader.load("pickaxe")));
        TRAILS.put("boss", new ArrayList<>(TrailFileLoader.load("boss")));
        TRAILS.put("conduit1", new ArrayList<>(TrailFileLoader.load("conduit1")));
        TRAILS.put("conduit2", new ArrayList<>(TrailFileLoader.load("conduit2")));
        TRAILS.put("conduit3", new ArrayList<>(TrailFileLoader.load("conduit3")));
        TRAILS.put("conduit4", new ArrayList<>(TrailFileLoader.load("conduit4")));
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (!trailEnabled || activeTrail.isEmpty()) return;

            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;

            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());
            int total = activeTrail.size();

            for (int i = 0; i < total; i++) {
                BlockPos pos = activeTrail.get(i);
                float hue = (float) i / total;
                Color color = Color.getHSBColor(hue, 1f, 1f);

                assert matrices != null;
                renderCube(
                        matrices, buffer, camera, pos,
                        color.getRed() / 255f,
                        color.getGreen() / 255f,
                        color.getBlue() / 255f
                );
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!trailEnabled || client.world == null || client.player == null) {
                activeTrail = Collections.emptyList();
                return;
            }

            updateTrailBasedOnConditions();
        });

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.CONDUIT) {
                if (pos.equals(conduit1)) {
                    hasreturnedfromconduit1 = false;
                } else if (pos.equals(conduit2)) {
                    hasreturnedfromconduit2 = false;
                } else if (pos.equals(conduit3)) {
                    hasreturnedfromconduit3 = false;
                } else if (pos.equals(conduit4)) {
                    hasreturnedfromconduit4 = false;
                }

            }
        });
    }

    private static boolean hasIronPickaxe(MinecraftClient client) {
        assert client.player != null;
        for (ItemStack stack : client.player.getInventory().main) {
            if (stack.getItem() == Items.IRON_PICKAXE) return true;
        }
        return false;
    }

    private static void updateTrailBasedOnConditions() {
        MinecraftClient client = MinecraftClient.getInstance();


        // Boss room detection
        if (IsWithinSpawner(client)) {
            hasenteredbossroom = true;
        }

        activeTrail = Collections.emptyList();

        // Pickaxe / Boss priority
        if (!hasIronPickaxe(client)) {
            activeTrail = TRAILS.get("pickaxe");
            return;
        }

        if (!hasenteredbossroom) {
            activeTrail = TRAILS.get("boss");
            return;
        }

        // -------- Conduit 1 --------
        List<BlockPos> conduit1Forward = TRAILS.get("conduit1");
        List<BlockPos> conduit1Backward = new ArrayList<>(conduit1Forward);
        Collections.reverse(conduit1Backward);

            assert client.world != null;
            if (client.world.getBlockState(conduit1).isOf(Blocks.CONDUIT)) {
            activeTrail = conduit1Forward;
        } else if (!hasreturnedfromconduit1) {
            if (IsWithinSpawner(client)) {
                hasreturnedfromconduit1 = true;
            } else {
                activeTrail = conduit1Backward;
                return;
            }
        }

        // -------- Conduit 2 --------
        List<BlockPos> conduit2Forward = TRAILS.get("conduit2");
        List<BlockPos> conduit2Backward = new ArrayList<>(conduit2Forward);
        Collections.reverse(conduit2Backward);

        if (client.world.getBlockState(conduit2).isOf(Blocks.CONDUIT)) {
            activeTrail = conduit2Forward;
        } else if (!hasreturnedfromconduit2) {
            if (IsWithinSpawner(client)) {
                hasreturnedfromconduit2 = true;
            } else {
                activeTrail = conduit2Backward;
                return;
            }
        }

        // -------- Conduit 3 --------
        List<BlockPos> conduit3Forward = TRAILS.get("conduit3");
        List<BlockPos> conduit3Backward = new ArrayList<>(conduit3Forward);
        Collections.reverse(conduit3Backward);

        if (client.world.getBlockState(conduit3).isOf(Blocks.CONDUIT)) {
            activeTrail = conduit3Forward;
        } else if (!hasreturnedfromconduit3) {
            if (IsWithinSpawner(client)) {
                hasreturnedfromconduit3 = true;
            } else {
                activeTrail = conduit3Backward;
                return;
            }
        }

        // -------- Conduit 4 --------
        List<BlockPos> conduit4Forward = TRAILS.get("conduit4");
        List<BlockPos> conduit4Backward = new ArrayList<>(conduit4Forward);
        Collections.reverse(conduit4Backward);

        assert client.world != null;
        if (client.world.getBlockState(conduit4).isOf(Blocks.CONDUIT)) {
            activeTrail = conduit4Forward;
        } else if (!hasreturnedfromconduit4) {
            if (IsWithinSpawner(client)) {
                hasreturnedfromconduit4 = true;
            } else {
                activeTrail = conduit4Backward;
            }
        }
    }


    public static boolean isEnabled() {
        return trailEnabled;
    }

    public static void toggleTrail() {
        trailEnabled = !trailEnabled;

        if (trailEnabled) {
            hasenteredbossroom = false;
            hasreturnedfromconduit1 = false;
            hasreturnedfromconduit2 = false;
            hasreturnedfromconduit3 = false;
            hasreturnedfromconduit4 = false;
            if (TRAILS.isEmpty()) init();
            ChatUtils.sendClientMessage("Water Trail: ON");
        } else {
            activeTrail = Collections.emptyList();
            ChatUtils.sendClientMessage("Water Trail: OFF");
        }
    }
}
