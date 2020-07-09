package com.tfc.optimizationmodtest.events;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.client.gui.widget.Widget.UNSET_FG_COLOR;
import static net.minecraft.client.gui.widget.Widget.WIDGETS_LOCATION;

public class VideoSettings {
	static double mx=0;
	static double my=0;
	
	//TERRAIN RENDERING
		public static final int renderModeMin=0;
	public static int renderMode=7;
		public static final int renderModeMax=15;
	
	//ENTITIES
	public static boolean alwaysRender=false;
	public static boolean render2D=false;
	public static boolean checkEntityRange=true;
	public static boolean alwaysCheckFrustrum=false;
	
	//TILE ENTITIES
		public static final double teRenderDistanceAmplifierMin=0;
	public static double teRenderDistanceAmplifier=1;
		public static final double teRenderDistanceAmplifierMax=10;
	
	public static void onDrawGUI(GuiScreenEvent event) {
		if (event.getGui() instanceof VideoSettingsScreen) {
			mx=Minecraft.getInstance().mouseHelper.getMouseX()/Minecraft.getInstance().gameSettings.guiScale;
			my=Minecraft.getInstance().mouseHelper.getMouseY()/Minecraft.getInstance().gameSettings.guiScale;
			if (event instanceof GuiScreenEvent.DrawScreenEvent) {
				int width=80;
				int height=20;
				int y=37;
				y+=handleButtonRender(0,y,width,height,"Render Mode:"+renderMode,event.getGui());
				y+=handleButtonRender(0,y,width,height,"Always Render Entities:"+alwaysRender,event.getGui());
				y+=handleButtonRender(0,y,width,height,"Entities Are 2D:"+render2D,event.getGui());
				y+=handleButtonRender(0,y,width,height,"Always Check View For Entities:"+alwaysCheckFrustrum,event.getGui());
				y+=handleButtonRender(0,y,width,height,"Check Range For Entities:"+checkEntityRange,event.getGui());
				y+=handleSliderRender(0,y,width,height,"TE Render Distance:"+teRenderDistanceAmplifier,teRenderDistanceAmplifier,event.getGui(),teRenderDistanceAmplifierMin,teRenderDistanceAmplifierMax);
				
			} else if (event instanceof GuiScreenEvent.MouseClickedEvent.Pre) {
				int y=36;
				int width=80;
				int height=20;
				int press=handleButtonPress(0,y,width,height,((GuiScreenEvent.MouseClickedEvent)event).getButton());
				switch (press) {
					case 1:
						renderMode++; if (renderMode>renderModeMax) renderMode=renderModeMin;
						break;
					case 2:
						renderMode--; if (renderMode<renderModeMin) renderMode=renderModeMax;
						break;
					default:break;
				}
				y+=height+3;
				press=handleButtonPress(0,y,width,height,((GuiScreenEvent.MouseClickedEvent)event).getButton());
				if (press!=0) alwaysRender=!alwaysRender;
				y+=height+3;
				press=handleButtonPress(0,y,width,height,((GuiScreenEvent.MouseClickedEvent)event).getButton());
				if (press!=0) render2D=!render2D;
				y+=height+3;
				press=handleButtonPress(0,y,width,height,((GuiScreenEvent.MouseClickedEvent)event).getButton());
				if (press!=0) alwaysCheckFrustrum=!alwaysCheckFrustrum;
				y+=height+3;
				press=handleButtonPress(0,y,width,height,((GuiScreenEvent.MouseClickedEvent)event).getButton());
				if (press!=0) checkEntityRange=!checkEntityRange;
				y+=height+3;
				press=handleButtonPress(0,y,width,height,((GuiScreenEvent.MouseClickedEvent)event).getButton());
				if (press!=0) {
					double x=mx-0;
					x+=1;
					x/=width;
					x*=100;
					x=Math.floor(x);
					x/=100;
					teRenderDistanceAmplifier=Math.max(teRenderDistanceAmplifierMin,Math.min(teRenderDistanceAmplifierMin+(x*teRenderDistanceAmplifierMax),teRenderDistanceAmplifierMax));
				}
			}
		}
	}
	
	public static int handleButtonPress(int x,int y,int width,int height, int button) {
		boolean hoveredX=x-1<mx&&mx<x+width-1;
		boolean hoveredY=y-1<my&&my<y+height-1;
		boolean pressed=button==0;
		boolean pressed2=button==1;
		if (hoveredX&&hoveredY) {
			if (pressed) {
				return 1;
			} else if (pressed2) {
				return 2;
			}
		}
		return 0;
	}
	
	public static int handleButtonRender(int x, int y, int width, int height, String text, Screen gui) {
		boolean hoveredX=x-1<mx&&mx<x+width-1;
		boolean hoveredY=y-1<my&&my<y+height-1;
		float scale=Minecraft.getInstance().fontRenderer.getStringWidth(text)/(float)width;
		RenderSystem.pushMatrix();
		scale=Math.min(1,(1/(scale+0.1f)));
		renderButton(
				0,
				0,
				0,
				255,
				hoveredX&&hoveredY,
				gui,
				width,
				height,
				x,
				y,
				text,
				true,
				scale
		);
		RenderSystem.popMatrix();
		return height+3;
	}
	
	public static int handleSliderRender(int x, int y, int width, int height, String text, double value, Screen gui, double min, double max) {
		boolean hoveredX=x-1<mx&&mx<x+width+1;
		boolean hoveredY=y-1<my&&my<y+height+1;
		float scale=Minecraft.getInstance().fontRenderer.getStringWidth(text)/(float)width;
		RenderSystem.pushMatrix();
		scale=Math.min(1,(1/(scale+0.1f)));
		renderSlider(
				0,
				0,
				0,
				255,
				hoveredX&&hoveredY,
				gui,
				width,
				height,
				x,
				y,
				text,
				value,
				true,
				scale,
				min,
				max
		);
		RenderSystem.popMatrix();
		return height+3;
	}
	
	private static void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_, float alpha, boolean isHovered, Screen gui, int width, int height, int x, int y, String message, boolean active,float textScale) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int i = getYImage(isHovered,active);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		gui.blit(x, y, 0, 46 + i * 20, width / 2, height);
		gui.blit(x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);
		int j = getFGColor(active);
		RenderSystem.translatef(x+width/2f,y+(height-(8*textScale))/2f,0);
		RenderSystem.scalef(textScale,textScale,1);
		drawCenteredString(fontrenderer, message, 0, 0, j | MathHelper.ceil(alpha * 255.0F) << 24);
	}
	
	private static void renderSlider(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_, float alpha, boolean isHovered, Screen gui, int width, int height, int x, int y, String message, double value, boolean active,float textScale, double min, double max) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int i = getYImage(isHovered,false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		gui.blit(x, y, 0, 46 + i * 20, width / 2, height);
		gui.blit(x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);
		int sliderX=(int)((x+(((value-min)/max)*width))*0.95f);
		
		float scale=20;
		RenderSystem.pushMatrix();
		RenderSystem.scalef(1/scale,1,1);
		renderButton(p_renderButton_1_,p_renderButton_2_,p_renderButton_3_,alpha,isHovered,gui,width,height,(int)(sliderX*scale),y,"",active,textScale);
		RenderSystem.popMatrix();
		
		int j = getFGColor(active);
		RenderSystem.translatef(x+width/2f,y+(height-(8*textScale))/2f,0);
		RenderSystem.scalef(textScale,textScale,1);
		drawCenteredString(fontrenderer, message, 0, 0, j | MathHelper.ceil(alpha * 255.0F) << 24);
	}
	
	private static void drawCenteredString(FontRenderer p_drawCenteredString_1_, String p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_, int p_drawCenteredString_5_) {
		p_drawCenteredString_1_.drawStringWithShadow(p_drawCenteredString_2_, (float)(p_drawCenteredString_3_ - p_drawCenteredString_1_.getStringWidth(p_drawCenteredString_2_) / 2), (float)p_drawCenteredString_4_, p_drawCenteredString_5_);
	}
	
	private static int getYImage(boolean p_getYImage_1_, boolean active) {
		int i = 1;
		if (!active) {
			i = 0;
		} else if (p_getYImage_1_) {
			i = 2;
		}
		
		return i;
	}
	
	private static int getFGColor(boolean active) {
		return active ? 16777215 : 10526880; // White : Light Grey
	}
}
