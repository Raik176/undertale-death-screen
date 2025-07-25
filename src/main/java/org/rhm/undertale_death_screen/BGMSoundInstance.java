package org.rhm.undertale_death_screen;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class BGMSoundInstance extends AbstractTickableSoundInstance {
    private int fadeDir;
    private int fade;

    public BGMSoundInstance(SoundEvent soundEvent) {
        super(soundEvent, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 0;
        this.volume = 1;
        this.relative = true;
    }

    @Override
    public void tick() {
        this.fade += this.fadeDir;
        this.volume = Mth.clamp((float) this.fade / 5, 0, 1);
    }

    public void fadeIn() {
        this.fade = Math.max(0, this.fade);
        this.fadeDir = 1;
    }
}