package com.tfc.optimizationmodtest.mixin_code;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ChestRenderer {
	public static boolean isChristmas;
	public static void renderChest(TileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, ModelRenderer singleLid, ModelRenderer singleLatch, ModelRenderer singleBottom, ModelRenderer doubleBottom, ModelRenderer doubleLid, ModelRenderer doubleBottomRight, ModelRenderer doubleLidRight) {
		Minecraft.getInstance().getProfiler().startSection("Chests");
//		World world = tileEntityIn.getWorld();
//		boolean flag = world != null;
//		BlockState blockstate = flag ? tileEntityIn.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
//		ChestType chesttype = blockstate.has(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
//		Block block = blockstate.getBlock();
//		Minecraft.getInstance().getProfiler().startSection(block.getRegistryName().toString());
//		if (block instanceof AbstractChestBlock) {
//			Minecraft.getInstance().getProfiler().startSection("CollectData");
//			matrixStackIn.push();
//			Minecraft.getInstance().getProfiler().startSection("CheckSide");
//			AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock) block;
//			TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper;
//			if (flag) icallbackwrapper = abstractchestblock.func_225536_a_(blockstate, world, tileEntityIn.getPos(), true);
//			else icallbackwrapper = TileEntityMerger.ICallback::func_225537_b_;
//
//			Minecraft.getInstance().getProfiler().endStartSection("GetLidRotation");
//			float f1 = icallbackwrapper.apply(ChestBlock.func_226917_a_((IChestLid) tileEntityIn)).get(partialTicks);
//			f1 = 1.0F - f1;
//			Material material = getMaterial(tileEntityIn, chesttype);
//			Material material2 = getMaterial(tileEntityIn, ChestType.SINGLE);
//			Material material3 = getMaterial(tileEntityIn, ChestType.RIGHT);
//			f1 = 1.0F - f1 * f1 * f1;
//
//			Minecraft.getInstance().getProfiler().endStartSection("GetBuffer");
//			IVertexBuilder ivertexbuilder = material.getBuffer(bufferIn, RenderType::getEntityCutout);
//			IVertexBuilder ivertexbuilderLock = material2.getBuffer(bufferIn, RenderType::getEntityCutout);
//			IVertexBuilder ivertexbuilderRight = material3.getBuffer(bufferIn, RenderType::getEntityCutout);
//			Minecraft.getInstance().getProfiler().endSection();
//
//			Minecraft.getInstance().getProfiler().endStartSection("SetupMatrix");
//			Direction dir=blockstate.get(ChestBlock.FACING);
//			matrixStackIn.rotate(dir.getRotation());
//			matrixStackIn.rotate(new Quaternion(-90,0,0,true));
//			if (dir.equals(Direction.NORTH)) matrixStackIn.translate(-1,0,-1);
//			else if (dir.equals(Direction.EAST)) matrixStackIn.translate(-1,0,0);
//			else if (dir.equals(Direction.WEST)) matrixStackIn.translate(0,0,-1);
//
//			Minecraft.getInstance().getProfiler().endStartSection("Render");
//			if (!blockstate.has(ChestBlock.TYPE)||(blockstate.get(ChestBlock.TYPE).equals(ChestType.LEFT)||blockstate.get(ChestBlock.TYPE).equals(ChestType.SINGLE))) renderModels(matrixStackIn, ivertexbuilder, ivertexbuilderLock, ivertexbuilderRight, singleLid, singleLatch, singleBottom, f1, combinedLightIn, combinedOverlayIn, doubleLid, doubleBottom, doubleLidRight, doubleBottomRight, !chesttype.equals(ChestType.SINGLE));
//			matrixStackIn.pop();
//			Minecraft.getInstance().getProfiler().endSection();
//		}
//		Minecraft.getInstance().getProfiler().endSection();
		World world = tileEntityIn.getWorld();
		boolean flag = world != null;
		BlockState blockstate = flag ? tileEntityIn.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		Block block = blockstate.getBlock();
		Minecraft.getInstance().getProfiler().startSection(block.getRegistryName().toString());
		if (block instanceof AbstractChestBlock) {
			Minecraft.getInstance().getProfiler().startSection("GetChestType");
			ChestType chesttype = blockstate.has(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
			AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock)block;
			boolean flag1 = chesttype != ChestType.SINGLE;
			
			Minecraft.getInstance().getProfiler().endStartSection("SetupMatrix");
			matrixStackIn.push();
			float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
			
			Minecraft.getInstance().getProfiler().endStartSection("MergeLight");
			TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper;
			if (flag) {
				icallbackwrapper = abstractchestblock.func_225536_a_(blockstate, world, tileEntityIn.getPos(), true);
			} else {
				icallbackwrapper = TileEntityMerger.ICallback::func_225537_b_;
			}
			
			float f1 = icallbackwrapper.apply(ChestBlock.func_226917_a_((IChestLid)tileEntityIn)).get(partialTicks);
			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			int i = icallbackwrapper.apply(new DualBrightnessCallback<>()).applyAsInt(combinedLightIn);
			
			Minecraft.getInstance().getProfiler().endStartSection("Render");
			Material material = getMaterial(tileEntityIn, chesttype);
			IVertexBuilder ivertexbuilder = material.getBuffer(bufferIn, RenderType::getEntityCutout);

			Minecraft.getInstance().getProfiler().endStartSection("Render");
			if (flag1) {
				if (chesttype == ChestType.LEFT) {
					Material material2 = getMaterial(tileEntityIn, ChestType.SINGLE);
					IVertexBuilder ivertexbuilder2 = material2.getBuffer(bufferIn, RenderType::getEntityCutout);
					renderModels(matrixStackIn, ivertexbuilder, ivertexbuilder2, doubleLid, singleLatch, doubleBottom, f1, i, combinedOverlayIn);
				} else {
					renderModels(matrixStackIn, ivertexbuilder, doubleLidRight, singleLatch, doubleBottomRight, f1, i, combinedOverlayIn, false);
				}
			} else {
				renderModels(matrixStackIn, ivertexbuilder, singleLid, singleLatch, singleBottom, f1, i, combinedOverlayIn, true);
			}
			matrixStackIn.pop();
			Minecraft.getInstance().getProfiler().endSection();
		}
		Minecraft.getInstance().getProfiler().endSection();
		Minecraft.getInstance().getProfiler().endSection();
	}
	
	private static Material getMaterial(TileEntity tileEntity, ChestType chestType) {
		return Atlases.getChestMaterial(tileEntity, chestType, isChristmas);
	}
	
	public static void renderModels(MatrixStack matrixStackIn, IVertexBuilder bufferIn, ModelRenderer chestLid, ModelRenderer chestLatch, ModelRenderer chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn, boolean renderLock) {
		chestLid.rotateAngleX = -(lidAngle * ((float)Math.PI / 2F));
		chestLatch.rotateAngleX = chestLid.rotateAngleX;
		chestLid.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		chestBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		if (renderLock)
		chestLatch.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
	}
	
	public static void renderModels(MatrixStack matrixStackIn, IVertexBuilder bufferIn, IVertexBuilder bufferInLock, ModelRenderer chestLid, ModelRenderer chestLatch, ModelRenderer chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
		renderModels(matrixStackIn, bufferIn, chestLid, chestLatch, chestBottom, lidAngle, combinedLightIn, combinedOverlayIn, false);
		matrixStackIn.translate(-0.5f,0,0);
		chestLatch.render(matrixStackIn, bufferInLock, combinedLightIn, combinedOverlayIn);
	}
	
//	public static void renderModels(MatrixStack matrixStackIn, IVertexBuilder bufferIn, IVertexBuilder bufferLock, IVertexBuilder bufferRight, ModelRenderer chestLid, ModelRenderer chestLatch, ModelRenderer chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn, ModelRenderer doubleTop, ModelRenderer doubleBottom, ModelRenderer doubleTopR, ModelRenderer doubleBottomR, boolean isDouble) {
//		if (isDouble) {
//			doubleTop.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
//			doubleTopR.rotateAngleX = doubleTop.rotateAngleX;
//			chestLatch.rotateAngleX = doubleTop.rotateAngleX;
//			doubleTop.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
//			doubleBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
//			matrixStackIn.translate(-1,0,0);
//			doubleTopR.render(matrixStackIn, bufferRight, combinedLightIn, combinedOverlayIn);
//			doubleBottomR.render(matrixStackIn, bufferRight, combinedLightIn, combinedOverlayIn);
//			matrixStackIn.translate(0.5f,0,0);
//		} else {
//			chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
//			chestLatch.rotateAngleX = chestLid.rotateAngleX;
//			chestLid.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
//			chestBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
//		}
//		chestLatch.render(matrixStackIn, bufferLock, combinedLightIn, combinedOverlayIn);
//	}
}
