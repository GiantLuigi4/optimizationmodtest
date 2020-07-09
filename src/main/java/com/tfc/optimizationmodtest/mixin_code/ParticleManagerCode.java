package com.tfc.optimizationmodtest.mixin_code;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;

import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class ParticleManagerCode {
	public static void renderCustom(LightTexture lightTextureIn, MatrixStack matrixStackIn, float partialTicks, ActiveRenderInfo activeRenderInfoIn, Map<IParticleRenderType, Queue<Particle>> byType, TextureManager renderer) {
		Minecraft.getInstance().getProfiler().startSection("SetupRenderAttributes");
		lightTextureIn.enableLightmap();
		Runnable enable = () -> {
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.enableFog();
		};
		Minecraft.getInstance().getProfiler().endStartSection("SetupMatrix");
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
		
		Minecraft.getInstance().getProfiler().endStartSection("DefineCrashVariables");
		String renderType="";
		String particleString="";
		try {
			Minecraft.getInstance().getProfiler().endSection();
			for (IParticleRenderType iparticlerendertype : byType.keySet()) { // Forge: allow custom IParticleRenderType's
				renderType=iparticlerendertype.toString();
				
				Minecraft.getInstance().getProfiler().startSection("Collect");
				Iterable<Particle> iterable = byType.get(iparticlerendertype);
				
				Minecraft.getInstance().getProfiler().endStartSection("Render");
				if (iterable != null) {
					Minecraft.getInstance().getProfiler().startSection("Render:"+renderType);
					if (iparticlerendertype == IParticleRenderType.NO_RENDER) continue;
					Minecraft.getInstance().getProfiler().startSection("ResetRenderAttributes");
					enable.run(); //Forge: MC-168672 Make sure all render types have the correct GL state.
					
					Minecraft.getInstance().getProfiler().endStartSection("Prepare");
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuffer();
					
					Minecraft.getInstance().getProfiler().endStartSection("StarRender");
					iparticlerendertype.beginRender(bufferbuilder, renderer);
					
					Minecraft.getInstance().getProfiler().endStartSection("Iterate");
					for(Particle particle : iterable) {
						String s=particle.getClass().toString();
						s=s.substring(s.lastIndexOf('.'));
						Minecraft.getInstance().getProfiler().startSection("Render:"+s);
						particleString=s;
						particle.renderParticle(bufferbuilder, activeRenderInfoIn, partialTicks);
						Minecraft.getInstance().getProfiler().endSection();
					}
					
					Minecraft.getInstance().getProfiler().endStartSection("Finish");
					iparticlerendertype.finishRender(tessellator);
					Minecraft.getInstance().getProfiler().endSection();
				}
				Minecraft.getInstance().getProfiler().endSection();
				Minecraft.getInstance().getProfiler().endSection();
			}
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
			crashreportcategory.addDetail("Particle", particleString);
			crashreportcategory.addDetail("Particle Type", renderType);
			throw new ReportedException(crashreport);
		}
		
		Minecraft.getInstance().getProfiler().startSection("ResetMatrix");
		RenderSystem.popMatrix();
		
		Minecraft.getInstance().getProfiler().endStartSection("ResetRenderAttributes");
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.defaultAlphaFunc();
		lightTextureIn.disableLightmap();
		RenderSystem.disableFog();
		Minecraft.getInstance().getProfiler().endSection();
	}
	
	public static void renderVanilla(LightTexture lightTextureIn, MatrixStack matrixStackIn, float partialTicks, ActiveRenderInfo activeRenderInfoIn, Map<IParticleRenderType, Queue<Particle>> byType, TextureManager renderer) {
		lightTextureIn.enableLightmap();
		Runnable enable = () -> {
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.enableFog();
		};
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
		
		for(IParticleRenderType iparticlerendertype : byType.keySet()) { // Forge: allow custom IParticleRenderType's
			if (iparticlerendertype == IParticleRenderType.NO_RENDER) continue;
			enable.run(); //Forge: MC-168672 Make sure all render types have the correct GL state.
			
			Iterable<Particle> iterable = byType.get(iparticlerendertype);
			if (iterable != null) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				iparticlerendertype.beginRender(bufferbuilder, renderer);
				
				for(Particle particle : iterable) {
					try {
						particle.renderParticle(bufferbuilder, activeRenderInfoIn, partialTicks);
					} catch (Throwable throwable) {
						CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
						CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
						crashreportcategory.addDetail("Particle", particle::toString);
						crashreportcategory.addDetail("Particle Type", iparticlerendertype::toString);
						throw new ReportedException(crashreport);
					}
				}
				iparticlerendertype.finishRender(tessellator);
			}
		}
		
		RenderSystem.popMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.defaultAlphaFunc();
		lightTextureIn.disableLightmap();
		RenderSystem.disableFog();
	}
	
	public static void renderCustom2(LightTexture lightTextureIn, MatrixStack matrixStackIn, float partialTicks, ActiveRenderInfo activeRenderInfoIn, Map<IParticleRenderType, Queue<Particle>> byType, TextureManager renderer) {
		
		Minecraft.getInstance().getProfiler().startSection("SetupRenderAttributes");
		lightTextureIn.enableLightmap();
		Runnable enable = () -> {
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.enableFog();
		};
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		Minecraft.getInstance().getProfiler().endSection();
		try {
			for(IParticleRenderType iparticlerendertype : byType.keySet()) { // Forge: allow custom IParticleRenderType's
				Minecraft.getInstance().getProfiler().startSection("Render:"+iparticlerendertype.toString());
				if (iparticlerendertype == IParticleRenderType.NO_RENDER) {
					Minecraft.getInstance().getProfiler().endSection();
					continue;
				}
				
				Minecraft.getInstance().getProfiler().startSection("CollectParticles");
				Iterable<Particle> iterable = byType.get(iparticlerendertype);
				Minecraft.getInstance().getProfiler().endSection();
				if (iterable == null) {
//					Minecraft.getInstance().getProfiler().endSection();
					continue;
				}
				
				Minecraft.getInstance().getProfiler().startSection("Setup");
				enable.run(); //Forge: MC-168672 Make sure all render types have the correct GL state.
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				iparticlerendertype.beginRender(bufferbuilder, renderer);
				
				Minecraft.getInstance().getProfiler().endStartSection("Render");
				for(Particle particle : iterable) {
					Minecraft.getInstance().getProfiler().startSection(()->{
						String s=particle.getClass().getName();
						s=s.substring(s.lastIndexOf('.')+1);
						return s;
					});
					particle.renderParticle(bufferbuilder, activeRenderInfoIn, partialTicks);
					Minecraft.getInstance().getProfiler().endSection();
				}
				
				Minecraft.getInstance().getProfiler().endStartSection("Finish");
				iparticlerendertype.finishRender(tessellator);
				
				Minecraft.getInstance().getProfiler().endSection();
				Minecraft.getInstance().getProfiler().endSection();
			}
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
//			crashreportcategory.addDetail("Particle", particleString);
//			crashreportcategory.addDetail("Particle Type", renderType);
			throw new ReportedException(crashreport);
		}
		
		Minecraft.getInstance().getProfiler().startSection("Finish");
		RenderSystem.popMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.defaultAlphaFunc();
		lightTextureIn.disableLightmap();
		RenderSystem.disableFog();
		Minecraft.getInstance().getProfiler().endSection();
	}
	
	public static void renderCustom3(LightTexture lightTextureIn, MatrixStack matrixStackIn, float partialTicks, ActiveRenderInfo activeRenderInfoIn, Map<IParticleRenderType, Queue<Particle>> byType, TextureManager renderer) {
		lightTextureIn.enableLightmap();
		Runnable reset = () -> {
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.enableFog();
		};
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		for(IParticleRenderType iparticlerendertype : byType.keySet()) { // Forge: allow custom IParticleRenderType's
			if (iparticlerendertype == IParticleRenderType.NO_RENDER) continue;
			
			Iterable<Particle> iterable = byType.get(iparticlerendertype);
			if (iterable == null) continue;
			
			reset.run(); //Forge: MC-168672 Make sure all render types have the correct GL state.
			
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			iparticlerendertype.beginRender(bufferbuilder, renderer);
			
			for(Particle particle : iterable) {
//				try {
					particle.renderParticle(bufferbuilder, activeRenderInfoIn, partialTicks);
//				} catch (Throwable throwable) {
//					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
//					CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
//					crashreportcategory.addDetail("Particle", particle::toString);
//					crashreportcategory.addDetail("Particle Type", iparticlerendertype::toString);
//					throw new ReportedException(crashreport);
//				}
			}
			iparticlerendertype.finishRender(tessellator);
		}
		
		RenderSystem.popMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.defaultAlphaFunc();
		lightTextureIn.disableLightmap();
		RenderSystem.disableFog();
	}
	
	private static boolean alternator=false;
	
	public static void render(LightTexture lightTextureIn, MatrixStack matrixStackIn, float partialTicks, ActiveRenderInfo activeRenderInfoIn, Map<IParticleRenderType, Queue<Particle>> byType, TextureManager renderer) {
		Minecraft.getInstance().getProfiler().startSection("CustomThree");
		renderCustom3(lightTextureIn,matrixStackIn,partialTicks,activeRenderInfoIn,byType,renderer);
//		Minecraft.getInstance().getProfiler().endStartSection("CustomTwo");
//		renderCustom2(lightTextureIn,matrixStackIn,partialTicks,activeRenderInfoIn,byType,renderer);
//		Minecraft.getInstance().getProfiler().endStartSection("CustomOne");
//		renderCustom(lightTextureIn,matrixStackIn,partialTicks,activeRenderInfoIn,byType,renderer);
//		Minecraft.getInstance().getProfiler().endStartSection("Vanilla");
//		renderVanilla(lightTextureIn,matrixStackIn,partialTicks,activeRenderInfoIn,byType,renderer);
		Minecraft.getInstance().getProfiler().endSection();
		alternator=!alternator;
	}
}
