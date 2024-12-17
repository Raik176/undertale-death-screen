package org.rhm.undertale_death_screen.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.rhm.undertale_death_screen.UndertaleDeathScreenBase;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;
import net.neoforged.fml.common.Mod;

import java.util.function.Supplier;

@Mod(UndertaleDeathScreenCommon.MOD_ID)
public class UndertaleDeathScreenNeoforge {
	public UndertaleDeathScreenNeoforge(IEventBus eventBus, ModContainer container) {
		UndertaleDeathScreenCommon.init(new Impl());
		Impl.register(eventBus);
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
	};
}
