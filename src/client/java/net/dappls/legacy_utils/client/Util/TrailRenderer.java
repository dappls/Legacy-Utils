package net.dappls.legacy_utils.client.Util;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;

public class TrailRenderer {
    public static void renderCube(MatrixStack matrices, VertexConsumer buffer, Camera camera, BlockPos pos, float r, float g, float b) {
        matrices.push();
        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;
        matrices.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float minX = 0.35f, maxX = 0.65f;
        float minY = 0.35f, maxY = 0.65f;
        float minZ = 0.35f, maxZ = 0.65f;

        renderCuboid(buffer, matrix, r, g, b, minX, maxX, minY, maxY, minZ, maxZ);
        matrices.pop();
    }
    private static void renderCuboid(VertexConsumer buffer, Matrix4f matrix, float r, float g, float b, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        // Front face
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,1);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,1);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,1);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,1);


        // Back face
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,-1);
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,-1);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,-1);
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,0,-1);


        // Left face
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(-1,0,0);
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(-1,0,0);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(-1,0,0);
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(-1,0,0);


        // Right face
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(1,0,0);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(1,0,0);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(1,0,0);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(1,0,0);


        // Top face
        buffer.vertex(matrix, minX, maxY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,1,0);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,1,0);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,1,0);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,1,0);


        // Bottom face
        buffer.vertex(matrix, minX, minY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,-1,0);
        buffer.vertex(matrix, maxX, minY, minZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,-1,0);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,-1,0);
        buffer.vertex(matrix, minX, minY, maxZ).color(r,g,b, (float) 0.8).light(0xF000F0).normal(0,-1,0);
    }
}

