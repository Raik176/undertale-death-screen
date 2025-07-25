package org.rhm.undertale_death_screen.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.rhm.undertale_death_screen.UndertaleDeathScreenBase;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;

import java.nio.file.Path;
import java.util.function.Supplier;

public class UndertaleDeathScreenFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UndertaleDeathScreenCommon.init(new Impl());
    }

    public static final class Impl implements UndertaleDeathScreenBase {

        @Override
        public Supplier<SoundEvent> registerSoundEvent(String path) {
            ResourceLocation location = UndertaleDeathScreenCommon.id(path);
            SoundEvent event = Registry.register(
                    BuiltInRegistries.SOUND_EVENT,
                    location,
                    SoundEvent.createVariableRangeEvent(location)
            );
            return () -> event;
        }

        @Override
        public boolean isModLoaded(String id) {
            return FabricLoader.getInstance().isModLoaded(id);
        }

        @Override
        public Path getConfigDir() {
            return FabricLoader.getInstance().getConfigDir();
        }
    }
}
