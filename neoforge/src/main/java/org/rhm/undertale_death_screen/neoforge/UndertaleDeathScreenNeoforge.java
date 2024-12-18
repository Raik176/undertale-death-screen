package org.rhm.undertale_death_screen.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.rhm.undertale_death_screen.UndertaleDeathScreenBase;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;
import net.neoforged.fml.common.Mod;

import java.nio.file.Path;
import java.util.function.Supplier;

@Mod(UndertaleDeathScreenCommon.MOD_ID)
public class UndertaleDeathScreenNeoforge {
	public UndertaleDeathScreenNeoforge(IEventBus eventBus, ModContainer ignoredContainer) {
		UndertaleDeathScreenCommon.init(new Impl());
		Impl.register(eventBus);

		eventBus.addListener(this::clientSetup);
	}

	private void clientSetup(FMLClientSetupEvent event) {
		if (UndertaleDeathScreenCommon.getConfigScreen(null) != null) // hacky approach but eh
			ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, screen) -> UndertaleDeathScreenCommon.getConfigScreen(screen));
	}

	public static class Impl implements UndertaleDeathScreenBase {
		private static final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, UndertaleDeathScreenCommon.MOD_ID);

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
