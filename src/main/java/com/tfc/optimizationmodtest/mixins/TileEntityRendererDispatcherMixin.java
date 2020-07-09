package com.tfc.optimizationmodtest.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tfc.optimizationmodtest.BetterHashMap;
import com.tfc.optimizationmodtest.events.VideoSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

@Mixin(TileEntityRendererDispatcher.class)
public abstract class TileEntityRendererDispatcherMixin {
	@Shadow @Final private Map<TileEntityType<?>, TileEntityRenderer<?>> renderers;
	
	@Shadow public ActiveRenderInfo renderInfo;
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	private static <T extends TileEntity> void render(TileEntityRenderer<T> rendererIn, T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
		World world = tileEntityIn.getWorld();
		int i;
		if (world != null) {
			i = WorldRenderer.getCombinedLight(world, tileEntityIn.getPos());
		} else {
			i = 15728880;
		}
		
		rendererIn.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, i, OverlayTexture.NO_OVERLAY);
	}
	
	BetterHashMap<String,BetterHashMap<String,TileEntityRenderer<?>>> rendererList=new BetterHashMap<>();
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	private <E extends TileEntity> void register(TileEntityType<E> typeIn, TileEntityRenderer<E> rendererIn) {
		if (!rendererList.containsKey(Objects.requireNonNull(typeIn.getRegistryName()).getNamespace())) {
			rendererList.add(typeIn.getRegistryName().getNamespace(),new BetterHashMap<>());
		}
		rendererList.get(typeIn.getRegistryName().getNamespace()).add(typeIn.getRegistryName().getPath(),rendererIn);
	}
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	@Nullable
	public <E extends TileEntity> TileEntityRenderer<E> getRenderer(E tileEntityIn) {
		if (rendererList.containsKey(tileEntityIn.getType().getRegistryName().getNamespace())) {
			BetterHashMap map=rendererList.get(tileEntityIn.getType().getRegistryName().getNamespace());
			if (map.containsKey(tileEntityIn.getType().getRegistryName().getPath()))
			return (TileEntityRenderer<E>)map.get(tileEntityIn.getType().getRegistryName().getPath());
		}
		return (TileEntityRenderer<E>)this.renderers.get(tileEntityIn.getType());
	}
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	public <E extends TileEntity> void renderTileEntity(E tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
		if (tileEntityIn.getDistanceSq(this.renderInfo.getProjectedView().x, this.renderInfo.getProjectedView().y, this.renderInfo.getProjectedView().z) < (tileEntityIn.getMaxRenderDistanceSquared()* VideoSettings.teRenderDistanceAmplifier)) {
			TileEntityRenderer<E> tileEntityRenderer = this.getRenderer(tileEntityIn);
			if (tileEntityRenderer != null) {
				runCrashReportable(tileEntityIn, () -> render(tileEntityRenderer, tileEntityIn, partialTicks, matrixStackIn, bufferIn));
			}
		}
	}
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	private static void runCrashReportable(TileEntity tileEntityIn, Runnable runnableIn) {
		try {
			runnableIn.run();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
			tileEntityIn.addInfoToCrashReport(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}
}
