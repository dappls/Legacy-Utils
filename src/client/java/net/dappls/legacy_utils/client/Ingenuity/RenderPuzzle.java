package net.dappls.legacy_utils.client.Ingenuity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
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
            if (client.world == null || pos == null) return;

            MatrixStack matrices = context.matrixStack();
            Vec3d cameraPos = context.camera().getPos();

            double x = pos.getX() - cameraPos.x;
            double y = pos.getY() - cameraPos.y + 0.01; // slightly above block
            double z = pos.getZ() - cameraPos.z;

            renderGlowingGrid(matrices, x, y, z);
        });
    }

    private static void renderGlowingGrid(MatrixStack matrices, double x, double y, double z) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(3.0F);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumerProvider.Immediate buffer = client.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderLayer.getLines());

        float r = 0.2f, g = 0.8f, b = 1.0f, a = 0.8f;
        // Draw a 1x1x1 cube outline
        float x1 = (float) x;
        float y1 = (float) y;
        float z1 = (float) z;
        float x2 = (float) (x + 1);
        float y2 = (float) (y + 1);
        float z2 = (float) (z + 1);

        // Bottom square
        vertexConsumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x2, y1, z1).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x2, y1, z1).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x2, y1, z2).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x2, y1, z2).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x1, y1, z2).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x1, y1, z2).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0f, 1f, 0f);

        // Top square
        vertexConsumer.vertex(matrix, x1, y2, z1).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x2, y2, z1).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x2, y2, z1).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x1, y2, z2).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x1, y2, z2).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x1, y2, z1).color(r, g, b, a).normal(0f, 1f, 0f);

        // Vertical edges
        vertexConsumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x1, y2, z1).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x2, y1, z1).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x2, y2, z1).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x2, y1, z2).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0f, 1f, 0f);

        vertexConsumer.vertex(matrix, x1, y1, z2).color(r, g, b, a).normal(0f, 1f, 0f);
        vertexConsumer.vertex(matrix, x1, y2, z2).color(r, g, b, a).normal(0f, 1f, 0f);



        buffer.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}
