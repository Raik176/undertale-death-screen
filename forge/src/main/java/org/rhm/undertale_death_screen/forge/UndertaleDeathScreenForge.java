package org.rhm.undertale_death_screen.forge;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.rhm.undertale_death_screen.UndertaleDeathScreenBase;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod(UndertaleDeathScreenCommon.MOD_ID)
public class UndertaleDeathScreenForge {
	public UndertaleDeathScreenForge(FMLJavaModLoadingContext context) {
		UndertaleDeathScreenCommon.init(new Impl());
		Impl.register(context.getModEventBus());
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
	};
}
