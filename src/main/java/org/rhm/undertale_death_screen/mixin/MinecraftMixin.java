package org.rhm.undertale_death_screen.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.rhm.undertale_death_screen.DeathScreenAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void stopMusicOnDeathScreenRemoved(CallbackInfo ci) {
        if ((Object) screen instanceof DeathScreenAccess access) {
            access.undertale_death_animation$stopBackgroundMusic();
        }
    }
}
