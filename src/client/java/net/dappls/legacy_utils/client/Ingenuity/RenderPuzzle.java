package net.dappls.legacy_utils.client.Ingenuity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RenderPuzzle {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static BlockPos pos;

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (pos == null || client.world == null) return;

            // Try adding 'get' if the others failed.
            // 1.21.x Fabric API usually prefers these:
            MatrixStack matrices = context.matrices();
            Vec3d cameraPos = context.gameRenderer().getCamera().getCameraPos();

            double x = pos.getX() - cameraPos.x;
            double y = pos.getY() - cameraPos.y + 0.01;
            double z = pos.getZ() - cameraPos.z;

            renderGlowingGrid(matrices, context.consumers(), x, y, z);

        });
    }

    private static void renderGlowingGrid(MatrixStack matrices, VertexConsumerProvider consumers, double x, double y, double z) {
        if (consumers == null) return;


        RenderSystem.setupDefaultState();


        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayers.LINES);

        float r = 0.2f, g = 0.8f, b = 1.0f, a = 0.8f;

        float x1 = (float) x, y1 = (float) y, z1 = (float) z;
        float x2 = (float) (x + 1), y2 = (float) (y + 1), z2 = (float) (z + 1);

        drawBox(vertexConsumer, matrix, x1, y1, z1, x2, y2, z2, r, g, b, a);

    }

    private static void drawBox(VertexConsumer v, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        line(v, m, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(v, m, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(v, m, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(v, m, x1, y1, z2, x1, y1, z1, r, g, b, a);
        line(v, m, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(v, m, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(v, m, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(v, m, x1, y2, z2, x1, y2, z1, r, g, b, a);
        line(v, m, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(v, m, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(v, m, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(v, m, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private static void line(VertexConsumer v, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        v.vertex(m, x1, y1, z1).color(r, g, b, a).normal(0f, 1f, 0f);
        v.vertex(m, x2, y2, z2).color(r, g, b, a).normal(0f, 1f, 0f);
    }
}