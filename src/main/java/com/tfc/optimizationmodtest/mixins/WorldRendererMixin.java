package com.tfc.optimizationmodtest.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Set;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow @Final private Minecraft mc;
	@Shadow private double prevRenderSortX;
	@Shadow private double prevRenderSortY;
	@Shadow private double prevRenderSortZ;
	@Shadow private ChunkRenderDispatcher renderDispatcher;
	@Shadow @Final private VertexFormat blockVertexFormat;
	@Shadow @Final private ObjectList<WorldRenderer.LocalRenderInformationContainer> renderInfos;
	@Shadow private ClientWorld world;
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	private void checkMatrixStack(MatrixStack matrixStackIn) {
		if (!matrixStackIn.clear()) {
//			throw new IllegalStateException("Pose stack not empty");
		}
	}
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	private void renderBlockLayer(RenderType blockLayerIn, MatrixStack matrixStackIn, double xIn, double yIn, double zIn) {
		matrixStackIn.push();
		com.tfc.optimizationmodtest.mixin_code.WorldRenderer.renderChunk(blockLayerIn, matrixStackIn, xIn, yIn, zIn, mc, world, renderInfos, blockVertexFormat);
		matrixStackIn.pop();
	}
}
