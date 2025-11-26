package net.dappls.legacy_utils.client.Toggles;


import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.dappls.legacy_utils.client.Util.TrailRenderer.renderCube;


@Environment(EnvType.CLIENT)
public class SpiritParticleTrail {


    public enum SpiritTrailMode {
        OFF,
        SEWER1,
        SEWER2,
        SEWER3,
        SEWER4,
        VALLEY1,
        VALLEY2,
        VALLEY2_5,     // "VALLEY2.5"
        MINESHAFT2_5_1,
        MINESHAFT2_5_2,// "MINESHAFT2.5"
        VALLEY3,
        MINESHAFT3_1,
        MINESHAFT3_2;


        public SpiritTrailMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }
    public static SpiritTrailMode getCurrentMode() { return currentMode; }


    private static SpiritTrailMode currentMode  = SpiritTrailMode.OFF;
    private static final List<BlockPos> activeTrail = new ArrayList<>();


    // ---------------- PATH LISTS ----------------
    private static final List<BlockPos> Sewer1Path = new ArrayList<>();
    private static final List<BlockPos> Sewer2Path = new ArrayList<>();
    private static final List<BlockPos> Sewer3Path = new ArrayList<>();
    private static final List<BlockPos> Sewer4Path = new ArrayList<>();


    private static final List<BlockPos> Valley1Path = new ArrayList<>();
    private static final List<BlockPos> Valley2Path = new ArrayList<>();
    private static final List<BlockPos> Valley25Path = new ArrayList<>();
    private static final List<BlockPos> MineShaft25Path1 = new ArrayList<>();
    private static final List<BlockPos> MineShaft25Path2 = new ArrayList<>();
    private static final List<BlockPos> Valley3Path = new ArrayList<>();
    private static final List<BlockPos> MineShaft3Path1 = new ArrayList<>();
    private static final List<BlockPos> MineShaft3Path2 = new ArrayList<>();

    private static void init() {
        Sewer1Path.clear();
        Sewer2Path.clear();
        Sewer3Path.clear();
        Sewer4Path.clear();


        Valley1Path.clear();
        Valley2Path.clear();
        Valley25Path.clear();
        MineShaft25Path1.clear();
        MineShaft25Path2.clear();
        Valley3Path.clear();
        MineShaft3Path1.clear();
        MineShaft3Path2.clear();
        TrailFileLoader.SetPath("SpiritTrails/");
        Sewer1Path.addAll(TrailFileLoader.load("sewer1"));
        Sewer2Path.addAll(TrailFileLoader.load("sewer2"));
        Sewer3Path.addAll(TrailFileLoader.load("sewer3"));
        Sewer4Path.addAll(TrailFileLoader.load("sewer4"));

        Valley1Path.addAll(TrailFileLoader.load("valley1"));
        Valley2Path.addAll(TrailFileLoader.load("valley2"));
        Valley25Path.addAll(TrailFileLoader.load("valley2_5"));

        MineShaft25Path1.addAll(TrailFileLoader.load("mineshaft2.5_1"));
        MineShaft25Path2.addAll(TrailFileLoader.load("mineshaft2.5_2"));

        Valley3Path.addAll(TrailFileLoader.load("valley3"));
        MineShaft3Path1.addAll(TrailFileLoader.load("mineshaft3_1"));
        MineShaft3Path2.addAll(TrailFileLoader.load("mineshaft3_2"));
    }

    public static void register() {
        init();
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null || activeTrail.isEmpty()) return;

            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());
            int total = activeTrail.size();

            for (int i = 0; i < total; i++) {
                BlockPos pos = activeTrail.get(i);
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

        // Remove blocks player touches
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || currentMode == SpiritTrailMode.OFF) {
                activeTrail.clear();
                return;
            }
            removeTouchedTrailBlock();
        });
    }

    public static void updateActiveTrail() {
        activeTrail.clear();
        switch (currentMode) {
            case SEWER1 -> activeTrail.addAll(Sewer1Path);
            case SEWER2 -> activeTrail.addAll(Sewer2Path);
            case SEWER3 -> activeTrail.addAll(Sewer3Path);
            case SEWER4 -> activeTrail.addAll(Sewer4Path);


            case VALLEY1 -> activeTrail.addAll(Valley1Path);
            case VALLEY2 -> activeTrail.addAll(Valley2Path);
            case VALLEY2_5 -> activeTrail.addAll(Valley25Path);
            case MINESHAFT2_5_1 -> activeTrail.addAll(MineShaft25Path1);
            case MINESHAFT2_5_2 -> activeTrail.addAll(MineShaft25Path2);
            case VALLEY3 -> activeTrail.addAll(Valley3Path);
            case MINESHAFT3_1 -> activeTrail.addAll(MineShaft3Path1);
            case MINESHAFT3_2 -> activeTrail.addAll(MineShaft3Path2);


            case OFF -> {}
        }
    }


    public static void cycleMode() {
        if (MinecraftClient.getInstance().world != null) {
            currentMode = currentMode.next();
            updateActiveTrail();
        } else {
            MinecraftClient.getInstance().setScreen(null);
            ChatUtils.sendClientMessage("You are not within range of the puzzle!");
        }
    }

    private static void removeTouchedTrailBlock() {
        if (MinecraftClient.getInstance().player == null || activeTrail.isEmpty()) return;

        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
        int touchedIndex = -1;

        for (int i = 0; i < activeTrail.size(); i++) {
            BlockPos pos = activeTrail.get(i);
            if (playerPos.isWithinDistance(pos,1.0)) {
                touchedIndex = i;
                break;
            }
        }

        if (touchedIndex >= 0) {
            activeTrail.subList(0, touchedIndex + 1).clear();
        }
    }
}
