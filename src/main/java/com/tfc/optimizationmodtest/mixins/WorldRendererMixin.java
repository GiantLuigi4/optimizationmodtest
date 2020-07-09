package com.tfc.optimizationmodtest.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tfc.optimizationmodtest.mixin_code.WorldRendererCode;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow @Final private Minecraft mc;
	@Shadow @Final private VertexFormat blockVertexFormat;
	@Shadow @Final private ObjectList<WorldRenderer.LocalRenderInformationContainer> renderInfos;
	@Shadow private ClientWorld world;
	@Shadow private ChunkRenderDispatcher renderDispatcher;
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
		WorldRendererCode.renderChunks(blockLayerIn, matrixStackIn, xIn, yIn, zIn, mc, world, renderInfos, blockVertexFormat, renderDispatcher);
		matrixStackIn.pop();
	}
}
