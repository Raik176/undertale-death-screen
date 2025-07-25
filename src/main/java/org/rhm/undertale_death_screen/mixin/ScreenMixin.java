package org.rhm.undertale_death_screen.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.rhm.undertale_death_screen.DeathScreenAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "removed", at = @At("HEAD"))
    private void removed(CallbackInfo ci) {
        if ((Object) this instanceof DeathScreenAccess access) {
            access.undertale_death_animation$stopBackgroundMusic();
        }
    }
}
