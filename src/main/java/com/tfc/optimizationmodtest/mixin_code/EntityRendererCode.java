package com.tfc.optimizationmodtest.mixin_code;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class EntityRendererCode {
	public static RenderNameplateEvent renderName(Entity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		Minecraft.getInstance().getProfiler().startSection("Render:"+entityIn.getType().getRegistryName());
		RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(entityIn, entityIn.getDisplayName().getFormattedText(), Minecraft.getInstance().getRenderManager().getRenderer(entityIn), matrixStackIn, bufferIn, packedLightIn);
		MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
		return renderNameplateEvent;
	}
	
	public static void renderPost() {
		Minecraft.getInstance().getProfiler().endSection();
	}
}
