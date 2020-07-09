package com.tfc.optimizationmodtest.mixin_code;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tfc.optimizationmodtest.events.VideoSettings;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicReference;

public class WorldRendererCode {
	static double prevRenderSortX=0;
	static double prevRenderSortY=0;
	static double prevRenderSortZ=0;
	public static void renderChunks(RenderType blockLayerIn, MatrixStack matrixStackIn, double xIn, double yIn, double zIn, Minecraft mc, World world, ObjectList<net.minecraft.client.renderer.WorldRenderer.LocalRenderInformationContainer> renderInfos, VertexFormat blockVertexFormat, ChunkRenderDispatcher renderDispatcher) {
		blockLayerIn.setupRenderState();
		if (blockLayerIn == RenderType.getTranslucent()) {
			mc.getProfiler().startSection("TranslucentSort");
			double d0 = xIn - prevRenderSortX;
			double d1 = yIn - prevRenderSortY;
			double d2 = zIn - prevRenderSortZ;
			if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
				prevRenderSortX = xIn;
				prevRenderSortY = yIn;
				prevRenderSortZ = zIn;
				int i = 0;
				
				for(WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : renderInfos) {
					if (i < 15 && worldrenderer$localrenderinformationcontainer.renderChunk.resortTransparency(blockLayerIn, renderDispatcher)) {
						++i;
					}
				}
			}
			mc.getProfiler().endSection();
		}
		
		mc.getProfiler().startSection(()->"Render:"+blockLayerIn);
		boolean flag = blockLayerIn != RenderType.getTranslucent();
		ObjectListIterator<WorldRenderer.LocalRenderInformationContainer> objectlistiterator = renderInfos.listIterator(flag ? 0 : renderInfos.size());
		
		while(true) {
			if (flag) {
				if (!objectlistiterator.hasNext()) {
					break;
				}
			} else if (!objectlistiterator.hasPrevious()) {
				break;
			}
			
			WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer1 = flag ? objectlistiterator.next() : objectlistiterator.previous();
			ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = worldrenderer$localrenderinformationcontainer1.renderChunk;
			if (!chunkrenderdispatcher$chunkrender.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
				mc.getProfiler().startSection("GetBuffer");
				VertexBuffer vertexbuffer = chunkrenderdispatcher$chunkrender.getVertexBuffer(blockLayerIn);
				mc.getProfiler().endSection();
				matrixStackIn.push();
				BlockPos blockpos = chunkrenderdispatcher$chunkrender.getPosition();
				matrixStackIn.translate((double)blockpos.getX() - xIn, (double)blockpos.getY() - yIn, (double)blockpos.getZ() - zIn);
				mc.getProfiler().startSection("Draw");
				
				mc.getProfiler().startSection("Bind");
				vertexbuffer.bindBuffer();
				
				mc.getProfiler().endStartSection("SetupState");
				blockVertexFormat.setupBufferState(0L);
				
				mc.getProfiler().endStartSection("Draw");
				vertexbuffer.draw(matrixStackIn.getLast().getMatrix(), VideoSettings.renderMode);
				mc.getProfiler().endSection();
				mc.getProfiler().endSection();
				matrixStackIn.pop();
			}
		}
		
		VertexBuffer.unbindBuffer();
		RenderSystem.clearCurrentColor();
		blockVertexFormat.clearBufferState();
		blockLayerIn.clearRenderState();
		mc.getProfiler().endSection();
	}
}
