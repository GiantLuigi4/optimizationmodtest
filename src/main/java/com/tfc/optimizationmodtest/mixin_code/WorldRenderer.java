package com.tfc.optimizationmodtest.mixin_code;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tfc.optimizationmodtest.BetterHashMap;
import com.tfc.optimizationmodtest.BiObject;
import com.tfc.optimizationmodtest.CustomBuffer;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorldRenderer {
	public static void renderChunk(RenderType blockLayerIn, MatrixStack matrixStackIn, double xIn, double yIn, double zIn, Minecraft mc, World world, ObjectList renderInfos, VertexFormat blockVertexFormat) {
		blockLayerIn.setupRenderState();
		boolean flag = blockLayerIn != RenderType.getTranslucent();
		ObjectListIterator<net.minecraft.client.renderer.WorldRenderer.LocalRenderInformationContainer> objectlistiterator = renderInfos.listIterator(flag ? 0 : renderInfos.size());
		
		while (true) {
			if (flag) {
				if (!objectlistiterator.hasNext()) {
					break;
				}
			} else if (!objectlistiterator.hasPrevious()) {
				break;
			}
			
			net.minecraft.client.renderer.WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer1 = flag ? objectlistiterator.next() : objectlistiterator.previous();
			ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = worldrenderer$localrenderinformationcontainer1.renderChunk;
			if (
					!chunkrenderdispatcher$chunkrender.getCompiledChunk().isEmpty()&&
					!chunkrenderdispatcher$chunkrender.getCompiledChunk().isLayerEmpty(blockLayerIn)
			) {
				matrixStackIn.push();
				if (true) {
					BlockPos blockpos = chunkrenderdispatcher$chunkrender.getPosition();
					VertexBuffer vertexbuffer = chunkrenderdispatcher$chunkrender.getVertexBuffer(blockLayerIn);
					matrixStackIn.push();
					matrixStackIn.translate((double)blockpos.getX() - xIn, (double)blockpos.getY() - yIn, (double)blockpos.getZ() - zIn);
					matrixStackIn.scale(0,1,1);
					vertexbuffer.bindBuffer();
					blockVertexFormat.setupBufferState(0L);
					vertexbuffer.draw(matrixStackIn.getLast().getMatrix(), 7);
					matrixStackIn.pop();
				}
				if (true) {
					BlockPos blockpos = chunkrenderdispatcher$chunkrender.getPosition();
					VertexBuffer vertexbuffer = chunkrenderdispatcher$chunkrender.getVertexBuffer(blockLayerIn);
					matrixStackIn.push();
					matrixStackIn.translate((double)blockpos.getX() - xIn, (double)blockpos.getY() - yIn, (double)blockpos.getZ() - zIn);
					matrixStackIn.scale(1,0,1);
					vertexbuffer.bindBuffer();
					blockVertexFormat.setupBufferState(0L);
					vertexbuffer.draw(matrixStackIn.getLast().getMatrix(), 7);
					matrixStackIn.pop();
				}
				
				if (true) {
					BlockPos blockpos = chunkrenderdispatcher$chunkrender.getPosition();
					VertexBuffer vertexbuffer = chunkrenderdispatcher$chunkrender.getVertexBuffer(blockLayerIn);
					matrixStackIn.push();
					matrixStackIn.translate((double)blockpos.getX() - xIn, (double)blockpos.getY() - yIn, (double)blockpos.getZ() - zIn);
					matrixStackIn.scale(1,1,0);
					vertexbuffer.bindBuffer();
					blockVertexFormat.setupBufferState(0L);
					vertexbuffer.draw(matrixStackIn.getLast().getMatrix(), 7);
					matrixStackIn.pop();
				}
				matrixStackIn.pop();
			}
		}
		
		
		VertexBuffer.unbindBuffer();
		RenderSystem.clearCurrentColor();
		blockVertexFormat.clearBufferState();
//		mc.getProfiler().endSection();
		blockLayerIn.clearRenderState();
	}
}
