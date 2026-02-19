package net.dappls.legacy_utils.client.Util;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class TrailRenderer {

    public static void renderCube(MatrixStack matrices, VertexConsumer buffer, Camera camera, BlockPos pos, float r, float g, float b) {
        matrices.push();

        // Translate relative to camera
        double camX = camera.getCameraPos().x;
        double camY = camera.getCameraPos().y;
        double camZ = camera.getCameraPos().z;
        matrices.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

        // Use the Entry (peek) for both position and normal calls
        MatrixStack.Entry entry = matrices.peek();

        // Small cube bounds
        float min = 0.35f, max = 0.65f;

        renderCuboid(buffer, entry, r, g, b, min, max, min, max, min, max);

        matrices.pop();
    }

    private static void renderCuboid(VertexConsumer buffer, MatrixStack.Entry entry,
                                     float r, float g, float b,
                                     float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        float a = 0.8f;
        int light = 0xF000F0;

        // Front face (Z+)
        v(buffer, entry, minX, minY, maxZ, r, g, b, a, light, 0, 0, 1);
        v(buffer, entry, maxX, minY, maxZ, r, g, b, a, light, 0, 0, 1);
        v(buffer, entry, maxX, maxY, maxZ, r, g, b, a, light, 0, 0, 1);
        v(buffer, entry, minX, maxY, maxZ, r, g, b, a, light, 0, 0, 1);

        // Back face (Z-)
        v(buffer, entry, minX, minY, minZ, r, g, b, a, light, 0, 0, -1);
        v(buffer, entry, maxX, minY, minZ, r, g, b, a, light, 0, 0, -1);
        v(buffer, entry, maxX, maxY, minZ, r, g, b, a, light, 0, 0, -1);
        v(buffer, entry, minX, maxY, minZ, r, g, b, a, light, 0, 0, -1);

        // Left face (X-)
        v(buffer, entry, minX, minY, minZ, r, g, b, a, light, -1, 0, 0);
        v(buffer, entry, minX, minY, maxZ, r, g, b, a, light, -1, 0, 0);
        v(buffer, entry, minX, maxY, maxZ, r, g, b, a, light, -1, 0, 0);
        v(buffer, entry, minX, maxY, minZ, r, g, b, a, light, -1, 0, 0);

        // Right face (X+)
        v(buffer, entry, maxX, minY, minZ, r, g, b, a, light, 1, 0, 0);
        v(buffer, entry, maxX, minY, maxZ, r, g, b, a, light, 1, 0, 0);
        v(buffer, entry, maxX, maxY, maxZ, r, g, b, a, light, 1, 0, 0);
        v(buffer, entry, maxX, maxY, minZ, r, g, b, a, light, 1, 0, 0);

        // Top face (Y+)
        v(buffer, entry, minX, maxY, minZ, r, g, b, a, light, 0, 1, 0);
        v(buffer, entry, maxX, maxY, minZ, r, g, b, a, light, 0, 1, 0);
        v(buffer, entry, maxX, maxY, maxZ, r, g, b, a, light, 0, 1, 0);
        v(buffer, entry, minX, maxY, maxZ, r, g, b, a, light, 0, 1, 0);

        // Bottom face (Y-)
        v(buffer, entry, minX, minY, minZ, r, g, b, a, light, 0, -1, 0);
        v(buffer, entry, maxX, minY, minZ, r, g, b, a, light, 0, -1, 0);
        v(buffer, entry, maxX, minY, maxZ, r, g, b, a, light, 0, -1, 0);
        v(buffer, entry, minX, minY, maxZ, r, g, b, a, light, 0, -1, 0);
    }

    private static void v(VertexConsumer buffer, MatrixStack.Entry entry,
                          float x, float y, float z,
                          float r, float g, float b, float a,
                          int light, float nx, float ny, float nz) {

        // .vertex() uses the Matrix4f from the entry
        // .normal() uses the Entry itself
        buffer.vertex(entry.getPositionMatrix(), x, y, z)
                .color(r, g, b, a)
                .light(light)
                .normal(entry, nx, ny, nz);
    }
}