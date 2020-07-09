package com.tfc.optimizationmodtest.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tfc.optimizationmodtest.mixin_code.ParticleManagerCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
	
	@Shadow @Final private Map<IParticleRenderType, Queue<Particle>> byType;
	@Shadow @Final private TextureManager renderer;
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	public void renderParticles(MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, LightTexture lightTextureIn, ActiveRenderInfo activeRenderInfoIn, float partialTicks) {
		ParticleManagerCode.render(lightTextureIn,matrixStackIn,partialTicks,activeRenderInfoIn,byType,renderer);
	}
}
