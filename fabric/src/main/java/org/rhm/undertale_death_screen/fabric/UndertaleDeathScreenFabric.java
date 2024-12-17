package org.rhm.undertale_death_screen.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.rhm.undertale_death_screen.UndertaleDeathScreenBase;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;

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
	}
}
