package net.dappls.legacy_utils.Toggles;


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
public class DungeonParticleTrail {

    public enum DungeonTrailMode {
        OFF,
        LAMP1,
        LAMP2,
        LAMP3,
        WINDCHARGE;

        public DungeonTrailMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }
    public static DungeonTrailMode getCurrentMode() {
        return currentMode;
    }
    private static DungeonTrailMode currentMode = DungeonTrailMode.OFF;

    private static final List<BlockPos> Lamp1Path = new ArrayList<>();
    private static final List<BlockPos> Lamp2Path = new ArrayList<>();
    private static final List<BlockPos> Lamp3Path = new ArrayList<>();
    private static final List<BlockPos> WindChargePath = new ArrayList<>();

    private static final List<BlockPos> activeTrail = new ArrayList<>();

    public static void init() {
        Lamp1Path.clear();
        Lamp2Path.clear();
        Lamp3Path.clear();
        WindChargePath.clear();
        TrailFileLoader.SetPath("DungeonTrails/");
        Lamp1Path.addAll(TrailFileLoader.load("lamp1"));
        Lamp2Path.addAll(TrailFileLoader.load("lamp2"));
        Lamp3Path.addAll(TrailFileLoader.load("lamp3"));
        WindChargePath.addAll(TrailFileLoader.load("windcharge"));

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
            if (client.world == null || currentMode == DungeonTrailMode.OFF) {
                activeTrail.clear();
                return;
            }
            removeTouchedTrailBlock();
        });
    }

    public static void updateActiveTrail() {
        activeTrail.clear();
        switch (currentMode) {
            case LAMP1 -> activeTrail.addAll(Lamp1Path);
            case LAMP2 -> activeTrail.addAll(Lamp2Path);
            case LAMP3 -> activeTrail.addAll(Lamp3Path);
            case WINDCHARGE -> activeTrail.addAll(WindChargePath);
        }
    }


    public static void cycleMode() {
        if (MinecraftClient.getInstance().world != null &&
                MinecraftClient.getInstance().world.isPlayerInRange(10019, 136, 50537, 500)) {
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

