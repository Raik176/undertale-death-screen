package org.rhm.undertale_death_screen;

import net.minecraft.resources.ResourceLocation;
import org.rhm.undertale_death_screen.registry.SoundEventRegistry;

public class UndertaleDeathScreenCommon {
	private static UndertaleDeathScreenBase impl;
	public static final String MOD_ID = "undertale_death_screen";

	public static void init(UndertaleDeathScreenBase implementation) {
		if (impl == null) {
			impl = implementation;
			SoundEventRegistry.init();
		}
	}

	public static UndertaleDeathScreenBase getImplementation() {
		return impl;
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}
}
