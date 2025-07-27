package org.rhm.undertale_death_screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.rhm.undertale_death_screen.compat.ClothConfigCompatBase;
import org.rhm.undertale_death_screen.registry.SoundEventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;

public class UndertaleDeathScreenCommon {
    public static final String MOD_ID = "undertale_death_screen";
    public static final String MOD_NAME = "Undertale Death Screen";
    public static final Logger logger = LoggerFactory.getLogger(MOD_NAME);
    public static UndertaleDeathScreenBase impl;

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

    public static MutableComponent translatable(String path) {
        return Component.translatable(MOD_ID + "." + path);
    }

    private static Optional<ClothConfigCompatBase> getClothConfigCompat() {
        if (!impl.isModLoaded("cloth_config") && !impl.isModLoaded("cloth-config")) {
            return Optional.empty();
        }

        return ServiceLoader.load(ClothConfigCompatBase.class).findFirst();
    }

    public static boolean isConfigSupported() {
        return getClothConfigCompat().isPresent();
    }

    public static Screen getConfigScreen(Screen parent) {
        return getClothConfigCompat()
                .map(compat -> compat.getConfigScreen(parent))
                .orElse(null);
    }
}
