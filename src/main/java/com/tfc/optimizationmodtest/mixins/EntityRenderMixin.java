package com.tfc.optimizationmodtest.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tfc.optimizationmodtest.events.VideoSettings;
import com.tfc.optimizationmodtest.mixin_code.EntityRendererCode;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin {
	@Shadow protected abstract boolean canRenderName(Entity entity);
	
	@Shadow protected abstract void renderName(Entity entityIn, String displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn);
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	public boolean shouldRender(Entity livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
		if  (VideoSettings.alwaysRender) {
			return true;
		} else if (VideoSettings.checkEntityRange&&!livingEntityIn.isInRangeToRender3d(camX, camY, camZ)) {
			return false;
		} else if (!VideoSettings.alwaysCheckFrustrum&&livingEntityIn.ignoreFrustumCheck) {
			return true;
		} else {
			AxisAlignedBB axisalignedbb = livingEntityIn.getRenderBoundingBox().grow(0.5D);
			if (axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0D) {
				axisalignedbb = new AxisAlignedBB(livingEntityIn.getPosX() - 2.0D, livingEntityIn.getPosY() - 2.0D, livingEntityIn.getPosZ() - 2.0D, livingEntityIn.getPosX() + 2.0D, livingEntityIn.getPosY() + 2.0D, livingEntityIn.getPosZ() + 2.0D);
			}
			
			return camera.isBoundingBoxInFrustum(axisalignedbb);
		}
	}
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	public void render(Entity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStackIn.push();
		if (VideoSettings.render2D) {
			matrixStackIn.scale(0,1,1);
		}
		RenderNameplateEvent renderNameplateEvent=EntityRendererCode.renderName(entityIn,entityYaw,partialTicks,matrixStackIn,bufferIn,packedLightIn);
		if (renderNameplateEvent.getResult() != Event.Result.DENY && (renderNameplateEvent.getResult() == Event.Result.ALLOW || this.canRenderName(entityIn))) {
			this.renderName(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
		}
		EntityRendererCode.renderPost();
		matrixStackIn.pop();
	}
}
