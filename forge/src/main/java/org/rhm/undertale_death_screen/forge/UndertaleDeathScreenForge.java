package org.rhm.undertale_death_screen.forge;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
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

    @SuppressWarnings("removal")
    public UndertaleDeathScreenForge() {
        this(FMLJavaModLoadingContext.get());
    }

    public UndertaleDeathScreenForge(FMLJavaModLoadingContext context) {
        UndertaleDeathScreenCommon.init(new Impl());

        //? if >=1.21.6 {
        /*Impl.register(context.getModBusGroup());
        *///?} else
        Impl.register(context.getModEventBus());

        if (UndertaleDeathScreenCommon.isConfigSupported()) {
            //? if >=1.20.6 {
            context.registerExtensionPoint
            //?} else
            /*ModLoadingContext.get().registerExtensionPoint*/
                            (ConfigScreenHandler.ConfigScreenFactory.class,
                                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) ->
                                            UndertaleDeathScreenCommon.getConfigScreen(parent)));
        }
    }

    public static class Impl implements UndertaleDeathScreenBase {
        private static final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, UndertaleDeathScreenCommon.MOD_ID);

        //? if >=1.21.6 {
        /*public static void register(net.minecraftforge.eventbus.api.bus.BusGroup eventBus) {
            SOUND_EVENT_REGISTRY.register(eventBus);
        }
        *///?} else {
        public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
            SOUND_EVENT_REGISTRY.register(eventBus);
        }
        //?}

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
