package net.dappls.legacy_utils.client.SevenxSeven;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class GlowingOutlineRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private record RenderPos(BlockPos pos, float r, float g, float b, float a) {
    }
    private static final List<RenderPos> positions = new ArrayList<>();
    public static void ClearRendering() {
        positions.clear();
    }
    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MatrixStack matrices = context.matrixStack();
            Vec3d camPos = client.gameRenderer.getCamera().getPos();

            for (RenderPos rp : positions) {
                renderOutline(matrices, rp.pos, camPos, rp.r, rp.g, rp.b, rp.a);
            }
        });
    }

    public static void AddRenderPosition(BlockPos pos, float r, float g, float b, float a) {
        positions.add(new RenderPos(pos, r, g, b, a));
    }

    public static void clearAll() {
        positions.clear();
    }

    private static void renderOutline(MatrixStack matrices, BlockPos centerBottom, Vec3d camPos,
                                      float r, float g, float b, float a) {
        double width = 3, height = 87, depth = 3;
        double xOffset = (width - 1) / 2.0;
        double zOffset = (depth - 1) / 2.0;
        Vec3d min = new Vec3d(centerBottom.getX() - xOffset,
                centerBottom.getY(),
                centerBottom.getZ() - zOffset).subtract(camPos);
        Vec3d max = min.add(width, height, depth);
        Vec3d[] corners = new Vec3d[]{
                min,
                new Vec3d(max.x, min.y, min.z),
                new Vec3d(max.x, min.y, max.z),
                new Vec3d(min.x, min.y, max.z),
                new Vec3d(min.x, max.y, min.z),
                new Vec3d(max.x, max.y, min.z),
                max,
                new Vec3d(min.x, max.y, max.z)
        };
        int[][] edges = {
                {0,1},{1,2},{2,3},{3,0},
                {4,5},{5,6},{6,7},{7,4},
                {0,4},{1,5},{2,6},{3,7}
        };
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        VertexConsumerProvider.Immediate buffer = client.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderLayer.getLines());

        for (int[] e : edges) {
            Vec3d start = corners[e[0]];
            Vec3d end = corners[e[1]];
            vertexConsumer.vertex(matrices.peek().getPositionMatrix(),
                            (float) start.x, (float) start.y, (float) start.z)
                    .color(r, g, b, a)
                    .normal(0f, 1f, 0f);
            vertexConsumer.vertex(matrices.peek().getPositionMatrix(),
                            (float) end.x, (float) end.y, (float) end.z)
                    .color(r, g, b, a)
                    .normal(0f, 1f, 0f);
        }

        buffer.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
