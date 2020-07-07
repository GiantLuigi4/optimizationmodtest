package com.tfc.optimizationmodtest.mixins;

import com.mojang.bridge.game.GameVersion;
import com.tfc.optimizationmodtest.mixin_code.TitleScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.util.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;

@Mixin(MainMenuScreen.class)
public class TitleScreenMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
	
	}
}
