package com.tfc.optimizationmodtest.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tfc.optimizationmodtest.mixin_code.ChestRenderer;
import net.minecraft.block.*;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestTileEntityRenderer.class)
public abstract class ChestRendererMixin {
	@Shadow @Final private ModelRenderer leftLid;
	
	@Shadow @Final private ModelRenderer leftLatch;
	@Shadow @Final private ModelRenderer leftBottom;
	@Shadow @Final private ModelRenderer rightLid;
	private ModelRenderer leftLid2=null;
	private ModelRenderer leftBottom2=null;
	@Shadow @Final private ModelRenderer rightLatch;
	@Shadow @Final private ModelRenderer rightBottom;
	@Shadow @Final private ModelRenderer singleLid;
	@Shadow @Final private ModelRenderer singleLatch;
	@Shadow @Final private ModelRenderer singleBottom;
	@Shadow private boolean isChristmas;
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	public void render(TileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		ChestRenderer.isChristmas=isChristmas;
		ChestRenderer.renderChest(tileEntityIn,partialTicks,matrixStackIn,bufferIn,combinedLightIn,combinedOverlayIn,singleLid,singleLatch,singleBottom,leftBottom,leftLid,rightBottom,rightLid);
	}
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	private void renderModels(MatrixStack matrixStackIn, IVertexBuilder bufferIn, ModelRenderer chestLid, ModelRenderer chestLatch, ModelRenderer chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
		chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
		chestLatch.rotateAngleX = chestLid.rotateAngleX;
		chestLid.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		chestLatch.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		chestBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
	}
}
