package org.rhm.undertale_death_screen.forge;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.rhm.undertale_death_screen.UndertaleDeathScreenBase;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;

import java.nio.file.Path;
import java.util.function.Supplier;

@Mod(UndertaleDeathScreenCommon.MOD_ID)
public class UndertaleDeathScreenForge {
    public UndertaleDeathScreenForge(FMLJavaModLoadingContext context) {
        UndertaleDeathScreenCommon.init(new Impl());

        IEventBus eventBus = context.getModEventBus();
        Impl.register(eventBus);

        eventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        //idk what the older replacement for this is
        //? if >=1.20.6
        MinecraftForge.registerConfigScreen(UndertaleDeathScreenCommon::getConfigScreen);
    }

    public static class Impl implements UndertaleDeathScreenBase {
        private static final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, UndertaleDeathScreenCommon.MOD_ID);

        public static void register(IEventBus eventBus) {
            SOUND_EVENT_REGISTRY.register(eventBus);
        }

        @Override
        public Supplier<SoundEvent> registerSoundEvent(String path) {
            return SOUND_EVENT_REGISTRY.register(
                    path,
                    () -> SoundEvent.createVariableRangeEvent(UndertaleDeathScreenCommon.id(path))
            );
        }

        @Override
        public boolean isModLoaded(String id) {
            return ModList.get().isLoaded(id);
        }

        @Override
        public Path getConfigDir() {
            return FMLPaths.CONFIGDIR.get();
        }
    }
}
