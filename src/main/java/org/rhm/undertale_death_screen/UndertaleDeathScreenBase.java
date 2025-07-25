package org.rhm.undertale_death_screen;

import net.minecraft.sounds.SoundEvent;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface UndertaleDeathScreenBase {
    Supplier<SoundEvent> registerSoundEvent(String path);

    boolean isModLoaded(String id);

    Path getConfigDir();
}
