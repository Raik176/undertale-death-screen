package com.example.template;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TemplateModCommon {
	public static final String MOD_ID = "template";

	public static void init() {

	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}

	public static ResourceLocation vanillaId(String path) {
		return ResourceLocation.tryBuild("minecraft", path);
	}

	public static <T> ResourceKey<T> modKey(ResourceKey<Registry<T>> registry, String path) {
		return resourceKey(registry, id(path));
	}
	public static <T> ResourceKey<T> vanillaKey(ResourceKey<Registry<T>> registry, String path) {
		return resourceKey(registry, vanillaId(path));
	}

	public static <T> ResourceKey<T> resourceKey(ResourceKey<Registry<T>> registry, ResourceLocation id) {
		return ResourceKey.create(registry, id);
	}
}
