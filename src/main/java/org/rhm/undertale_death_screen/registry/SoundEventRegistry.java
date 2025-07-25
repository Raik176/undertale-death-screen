package org.rhm.undertale_death_screen.registry;

import net.minecraft.sounds.SoundEvent;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;

import java.util.function.Supplier;

public class SoundEventRegistry {
    public static final Supplier<SoundEvent> HEART_SHATTER_NOISES = UndertaleDeathScreenCommon.getImplementation()
            .registerSoundEvent("heart_shatter_noises");
    public static final Supplier<SoundEvent> DETERMINATION = UndertaleDeathScreenCommon.getImplementation()
            .registerSoundEvent("determination");

    public static void init() {
    }
}
