package com.tfc.optimizationmodtest.mixin_code;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.BufferBuilder;

import java.nio.ByteBuffer;
import java.util.List;

public class BufferBuilderCode {
	public static Pair<BufferBuilder.DrawState, ByteBuffer> getNext(BufferContext context) {
		BufferBuilder.DrawState bufferbuilder$drawstate = null;
		if (context.drawStates.size()>context.drawStateIndex) {
			bufferbuilder$drawstate=context.drawStates.get(context.drawStateIndex++);
		}
		if (bufferbuilder$drawstate!=null) {
			context.byteBuffer.position(context.uploadedBytes);
			context.uploadedBytes += bufferbuilder$drawstate.getVertexCount() * bufferbuilder$drawstate.getFormat().getSize();
			context.byteBuffer.limit(context.uploadedBytes);
			if (context.drawStateIndex == context.drawStates.size() && context.vertexCount == 0) {
				context.reset();
			}
			
			ByteBuffer bytebuffer = context.byteBuffer.slice();
			bytebuffer.order(context.byteBuffer.order()); // FORGE: Fix incorrect byte order
			context.byteBuffer.clear();
			return Pair.of(bufferbuilder$drawstate, bytebuffer);
		}
		ByteBuffer bytebuffer = context.byteBuffer.slice();
		bytebuffer.order(context.byteBuffer.order()); // FORGE: Fix incorrect byte order
		context.byteBuffer.clear();
		return Pair.of(bufferbuilder$drawstate, bytebuffer);
	}
	
	public static class BufferContext {
		private final ByteBuffer byteBuffer;
		public int uploadedBytes;
		public int drawStateIndex;
		private final int vertexCount;
		private final List<BufferBuilder.DrawState> drawStates;
		private final Runnable reset;
		
		public BufferContext(ByteBuffer byteBuffer, int uploadedBytes, int drawStateIndex, int vertexCount, List<BufferBuilder.DrawState> drawStates, Runnable reset) {
			this.byteBuffer = byteBuffer;
			this.uploadedBytes = uploadedBytes;
			this.drawStateIndex = drawStateIndex;
			this.vertexCount = vertexCount;
			this.drawStates = drawStates;
			this.reset=reset;
		}
		
		private void reset() {
			reset.run();
		}
	}
}
