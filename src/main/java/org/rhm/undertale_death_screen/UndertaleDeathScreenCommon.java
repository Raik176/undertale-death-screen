package org.rhm.undertale_death_screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.rhm.undertale_death_screen.compat.ClothConfigCompatBase;
import org.rhm.undertale_death_screen.registry.SoundEventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ServiceLoader;

public class UndertaleDeathScreenCommon {
	public static Config config;
	public static File configFile;
	private static UndertaleDeathScreenBase impl;
	public static final String MOD_ID = "undertale_death_screen";
	public static final Logger logger = LoggerFactory.getLogger("Undertale Death Screen");

	public static void init(UndertaleDeathScreenBase implementation) {
		if (impl == null) {
			impl = implementation;
			SoundEventRegistry.init();
		}
		configFile = impl.getConfigDir().resolve(MOD_ID + ".json").toFile();
		if (!configFile.exists()) {
			config = new Config(); // default values
			config.save(configFile); // generate default config
		} else {
			try (FileReader reader = new FileReader(configFile)) {
				JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
				DataResult<Pair<Config, JsonElement>> result = Config.CODEC.decode(JsonOps.INSTANCE, object);
				if (result.error().isPresent() /*? if >=1.20.6 {*/|| result.isError()/*?}*/ || result.result().isEmpty()) {
					logger.warn("Failed to parse configuration file. Using default configuration.");
					result.error().ifPresent(error -> logger.error("Error details: {}", error.message()));
					config = new Config();
				} else {
					config = result.result().get().getFirst();
				}
			} catch (IOException exception) {
				logger.error("IOException occurred while reading configuration file.", exception);
				logger.warn("Using default configuration due to file read error.");
				config = new Config();
				//warn
			}
		}
	}

	public static UndertaleDeathScreenBase getImplementation() {
		return impl;
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}

	public static MutableComponent translatable(String path) {
		return Component.translatable(MOD_ID + "." + path);
	}

	public static Screen getConfigScreen(Screen parent) {
		if (impl.isModLoaded("cloth_config") || impl.isModLoaded("cloth-config")) {
			ServiceLoader<ClothConfigCompatBase> loader = ServiceLoader.load(ClothConfigCompatBase.class);
			if (loader.findFirst().isEmpty()) {
				//huh??
				return null;
			}
			return loader.findFirst().get().getConfigScreen(parent);
		} else return null;
	}
}
