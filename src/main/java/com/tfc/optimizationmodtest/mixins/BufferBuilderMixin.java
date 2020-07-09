package com.tfc.optimizationmodtest.mixins;

import com.mojang.datafixers.util.Pair;
import com.tfc.optimizationmodtest.mixin_code.BufferBuilderCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin {
	@Shadow private boolean isDrawing;
	@Shadow private int drawMode;
	
	@Shadow protected abstract void setVertexFormat(VertexFormat vertexFormatIn);
	
	@Shadow @Nullable private VertexFormatElement vertexFormatElement;
	@Shadow private int vertexFormatIndex;
	@Shadow private ByteBuffer byteBuffer;
	
	@Shadow @Final private List<BufferBuilder.DrawState> drawStates;
	@Shadow private int drawStateIndex;
	@Shadow private int uploadedBytes;
	@Shadow private int vertexCount;
	
	@Shadow public abstract void reset();
	
	/**
	 * @author TFC The Flying Creeper
	 */
	@Overwrite
	public void begin(int glMode, VertexFormat format) {
		if (!this.isDrawing) {
			this.isDrawing = true;
			this.drawMode = glMode;
			this.setVertexFormat(format);
			this.vertexFormatElement = format.getElements().get(0);
			this.vertexFormatIndex = 0;
			((Buffer) this.byteBuffer).clear();
		}
	}
}
