package com.tfc.optimizationmodtest;

import com.google.common.collect.Sets;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class CustomBuffer implements IRenderTypeBuffer {
	public ArrayList<CustomVertexBuilder> builders=new ArrayList<>();
	
	@Override
	public IVertexBuilder getBuffer(RenderType p_getBuffer_1_) {
		CustomVertexBuilder builder=new CustomVertexBuilder(p_getBuffer_1_);
		builders.add(builder);
		return builder;
	}
	private static final RegionRenderCacheBuilder fixedBuilder = new RegionRenderCacheBuilder();
	public static final SortedMap<RenderType, CustomBufferBuilder> fixedBuffers = Util.make(new Object2ObjectLinkedOpenHashMap<>(), (p_228485_1_) -> {
		p_228485_1_.put(Atlases.getSolidBlockType(), fixedBuilder.getBuilder(RenderType.getSolid()));
		p_228485_1_.put(Atlases.getCutoutBlockType(), fixedBuilder.getBuilder(RenderType.getCutout()));
		p_228485_1_.put(Atlases.getBannerType(), fixedBuilder.getBuilder(RenderType.getCutoutMipped()));
		p_228485_1_.put(Atlases.getTranslucentBlockType(), fixedBuilder.getBuilder(RenderType.getTranslucent()));
		put(p_228485_1_, Atlases.getShieldType());
		put(p_228485_1_, Atlases.getBedType());
		put(p_228485_1_, Atlases.getShulkerBoxType());
		put(p_228485_1_, Atlases.getSignType());
		put(p_228485_1_, Atlases.getChestType());
		put(p_228485_1_, RenderType.getTranslucentNoCrumbling());
		put(p_228485_1_, RenderType.getGlint());
		put(p_228485_1_, RenderType.getEntityGlint());
		put(p_228485_1_, RenderType.getWaterMask());
		ModelBakery.DESTROY_RENDER_TYPES.forEach((p_228488_1_) -> {
			put(p_228485_1_, p_228488_1_);
		});
	});
	
	@OnlyIn(Dist.CLIENT)
	public static class RegionRenderCacheBuilder {
		private final Map<RenderType, CustomBufferBuilder> builders = RenderType.getBlockRenderTypes().stream().collect(Collectors.toMap((p_228369_0_) -> {
			return p_228369_0_;
		}, (p_228368_0_) -> {
			return new CustomBufferBuilder(p_228368_0_.getBufferSize(),p_228368_0_);
		}));
		
		public CustomBufferBuilder getBuilder(RenderType renderTypeIn) {
			return this.builders.get(renderTypeIn);
		}
		
		public void resetBuilders() {
			this.builders.values().forEach(BufferBuilder::reset);
		}
		
		public void discardBuilders() {
			this.builders.values().forEach(BufferBuilder::discard);
		}
	}
	
	private static void put(Object2ObjectLinkedOpenHashMap<RenderType, CustomBufferBuilder> mapBuildersIn, RenderType renderTypeIn) {
		mapBuildersIn.put(renderTypeIn, new CustomBufferBuilder(renderTypeIn.getBufferSize(),renderTypeIn));
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Impl extends CustomBuffer {
		protected final CustomBufferBuilder buffer;
		protected final Map<RenderType, CustomBufferBuilder> fixedBuffers;
		protected Optional<RenderType> lastRenderType = Optional.empty();
		protected final Set<BufferBuilder> startedBuffers = Sets.newHashSet();
		
		public ArrayList<BufferBuilder> builders=new ArrayList<>();
		
		public Impl(CustomBufferBuilder bufferIn, Map<RenderType, CustomBufferBuilder> fixedBuffersIn) {
			this.buffer = bufferIn;
			this.fixedBuffers = fixedBuffersIn;
		}
		
		public ArrayList<IElement> getAllElements() {
			ArrayList<IElement> elements=new ArrayList<>();
			for (BufferBuilder builder:builders) {
				elements.addAll(((CustomBufferBuilder)builder).elements);
			}
			elements.addAll(((CustomBufferBuilder)buffer).elements);
			return elements;
		}
		
		public IVertexBuilder getBuffer(RenderType p_getBuffer_1_) {
			Optional<RenderType> optional = p_getBuffer_1_.func_230169_u_();
			CustomBufferBuilder bufferbuilder = this.getBufferRaw(p_getBuffer_1_);
			builders.add(bufferbuilder);
			if (!Objects.equals(this.lastRenderType, optional)) {
				if (this.lastRenderType.isPresent()) {
					RenderType rendertype = this.lastRenderType.get();
					if (!this.fixedBuffers.containsKey(rendertype)) {
						this.finish(rendertype);
					}
				}
				
				if (this.startedBuffers.add(bufferbuilder)) {
					bufferbuilder.begin(p_getBuffer_1_.getDrawMode(), p_getBuffer_1_.getVertexFormat());
				}
				
				this.lastRenderType = optional;
			}
			
			return bufferbuilder;
		}
		
		private CustomBufferBuilder getBufferRaw(RenderType renderTypeIn) {
			return (this.fixedBuffers.getOrDefault(renderTypeIn, this.buffer));
		}
		
		public void finish() {
			this.lastRenderType.ifPresent((p_228464_1_) -> {
				IVertexBuilder ivertexbuilder = this.getBuffer(p_228464_1_);
				if (ivertexbuilder == this.buffer) {
					this.finish(p_228464_1_);
				}
				
			});
			
			for (RenderType rendertype : this.fixedBuffers.keySet()) {
				this.finish(rendertype);
			}
			
		}
		
		public void finish(RenderType renderTypeIn) {
			BufferBuilder bufferbuilder = this.getBufferRaw(renderTypeIn);
			boolean flag = Objects.equals(this.lastRenderType, renderTypeIn.func_230169_u_());
			if (flag || bufferbuilder != this.buffer) {
				if (this.startedBuffers.remove(bufferbuilder)) {
					renderTypeIn.finish(bufferbuilder, 0, 0, 0);
					if (flag) {
						this.lastRenderType = Optional.empty();
					}
					
				}
			}
		}
	}
	
	public static class CustomBufferBuilder extends BufferBuilder {
		public CustomBufferBuilder(int bufferSizeIn,RenderType type) {
			super(bufferSizeIn);
			this.type=type;
		}
		
		@Override
		public void begin(int glMode, VertexFormat format) {
//			try {
//				super.begin(glMode,format);
//			} catch (Exception err) {}
		}
		
		@Override
		public void finishDrawing() {
//			super.finishDrawing();
		}
		
		protected ArrayList<IElement> elements=new ArrayList<>();
		Vertex vertex=new Vertex();
		private RenderType type;
		
		@Override
		public IVertexBuilder lightmap(int lightmapUV) {
			vertex.lu=lightmapUV;
			vertex.lv=lightmapUV;
			return this;
		}
		
		@Override
		public IVertexBuilder overlay(int overlayUV) {
			return this;
		}
		
		@Override
		public IVertexBuilder pos(double x, double y, double z) {
			vertex.x=x;
			vertex.y=y;
			vertex.z=z;
			return this;
		}
		
		@Override
		public IVertexBuilder color(int red, int green, int blue, int alpha) {
			vertex.r=red;
			vertex.g=green;
			vertex.b=blue;
			vertex.a=alpha;
			return this;
		}
		
		@Override
		public IVertexBuilder tex(float u, float v) {
			vertex.u=u;
			vertex.v=v;
			return this;
		}
		
		@Override
		public IVertexBuilder overlay(int u, int v) {
			vertex.ou=u;
			vertex.ov=v;
			return this;
		}
		
		@Override
		public IVertexBuilder lightmap(int u, int v) {
			vertex.lu=u;
			vertex.lv=v;
			return this;
		}
		
		@Override
		public IVertexBuilder normal(float x, float y, float z) {
			vertex.nx=x;
			vertex.ny=y;
			vertex.nz=z;
			return this;
		}
		
		@Override
		public void endVertex() {
			elements.add(new VertexElement(vertex,this.type));
			vertex=new Vertex();
		}
		
		@Override
		public IVertexBuilder color(float red, float green, float blue, float alpha) {
			vertex.r=(int)(red*255);
			vertex.g=(int)(green*255);
			vertex.b=(int)(blue*255);
			vertex.a=(int)(alpha*255);
			return this;
		}
	}
	
	public static class CustomVertexBuilder implements IVertexBuilder {
		public ArrayList<Vertex> vertices=new ArrayList<>();
		public Vertex vertex=new Vertex();
		public RenderType type;
		
		public CustomVertexBuilder(RenderType type) {
			this.type = type;
		}
		
		@Override
		public IVertexBuilder lightmap(int lightmapUV) {
			vertex.lu=lightmapUV;
			vertex.lv=lightmapUV;
			return this;
		}
		
		@Override
		public IVertexBuilder overlay(int overlayUV) {
			return this;
		}
		
		@Override
		public IVertexBuilder pos(double x, double y, double z) {
			vertex.x=x;
			vertex.y=y;
			vertex.z=z;
			return this;
		}
		
		@Override
		public IVertexBuilder color(int red, int green, int blue, int alpha) {
			vertex.r=red;
			vertex.g=green;
			vertex.b=blue;
			vertex.a=alpha;
			return this;
		}
		
		@Override
		public IVertexBuilder tex(float u, float v) {
			vertex.u=u;
			vertex.v=v;
			return this;
		}
		
		@Override
		public IVertexBuilder overlay(int u, int v) {
			vertex.ou=u;
			vertex.ov=v;
			return this;
		}
		
		@Override
		public IVertexBuilder lightmap(int u, int v) {
			vertex.lu=u;
			vertex.lv=v;
			return this;
		}
		
		@Override
		public IVertexBuilder normal(float x, float y, float z) {
			vertex.nx=x;
			vertex.ny=y;
			vertex.nz=z;
			return this;
		}
		
		@Override
		public void endVertex() {
			vertices.add(vertex);
			vertex=new Vertex();
		}
		
		@Override
		public IVertexBuilder color(float red, float green, float blue, float alpha) {
			vertex.r=(int)(red*255);
			vertex.g=(int)(green*255);
			vertex.b=(int)(blue*255);
			vertex.a=(int)(alpha*255);
			return null;
		}
	}
	
	public static class Vertex {
		public double x;
		public double y;
		public double z;
		public int r;
		public int g;
		public int b;
		public int a;
		public float u;
		public float v;
		public float lu;
		public float lv;
		public int ou;
		public int ov;
		public float nx;
		public float ny;
		public float nz;
	}
	
	public static class VertexElement extends Vertex implements IElement {
		public VertexElement(Vertex vert,RenderType type) {
			this.x=vert.x;
			this.y=vert.y;
			this.z=vert.z;
			this.r=vert.r;
			this.g=vert.g;
			this.b=vert.b;
			this.a=vert.a;
			this.u=vert.u;
			this.v=vert.v;
			this.lv=vert.lv;
			this.lu=vert.lu;
			this.ou=vert.ou;
			this.ov=vert.ov;
			this.nx=vert.nx;
			this.ny=vert.ny;
			this.nz=vert.nz;
			this.type=type;
		}
		public RenderType type;
		
		@Override
		public void addToBuffer(IRenderTypeBuffer.Impl buffer) {
			try {
				buffer.getBuffer(this.type);
			} catch (Exception err) {}
		}
	}
	
	public interface IElement {
		public void addToBuffer(IRenderTypeBuffer.Impl buffer);
	}
}
