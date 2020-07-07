package com.tfc.optimizationmodtest.mixins;

import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityRenderer.class)
public class EntityRenderMixin {
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	public boolean shouldRender(Entity livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
		return true;
	}
}
