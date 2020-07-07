package com.tfc.optimizationmodtest.mixin_code;

import com.mojang.bridge.game.GameVersion;

import java.util.Date;

public class TitleScreen {
	public static GameVersion getVersion(GameVersion version) {
		return new GameVersion() {
			@Override
			public String getId() {
				return version.getId();
			}
			
			@Override
			public String getName() {
				return version.getName()+"_hello";
			}
			
			@Override
			public String getReleaseTarget() {
				return version.getReleaseTarget();
			}
			
			@Override
			public int getWorldVersion() {
				return version.getWorldVersion();
			}
			
			@Override
			public int getProtocolVersion() {
				return version.getProtocolVersion();
			}
			
			@Override
			public int getPackVersion() {
				return version.getPackVersion();
			}
			
			@Override
			public Date getBuildTime() {
				return version.getBuildTime();
			}
			
			@Override
			public boolean isStable() {
				return version.isStable();
			}
		};
	}
}
