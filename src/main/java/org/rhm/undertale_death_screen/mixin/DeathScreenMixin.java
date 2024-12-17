package org.rhm.undertale_death_screen.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.rhm.undertale_death_screen.DeathScreenAccess;
import org.rhm.undertale_death_screen.screen.UndertaleDeathScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen implements DeathScreenAccess {
    @Shadow @Final private boolean hardcore;

	@Shadow protected abstract void setButtonsActive(boolean bl);

	protected DeathScreenMixin(Component component) {
		super(component);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void init(CallbackInfo ci) {
		Minecraft.getInstance().setScreen(new UndertaleDeathScreen((DeathScreen) (Object) this));
	}

	@Inject(method = "renderDeathBackground", at = @At("TAIL"))
	private static void renderDeathBackground(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
		guiGraphics.fill(0, 0, i, j, 0xFF000000);
	}

	@Inject(method = "renderBackground", at = @At("TAIL"))
	private void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
		guiGraphics.fill(0, 0, i, j, 0xFF000000);
	}

	@Override
	public boolean undertale_death_animation$isHardcore() {
		return hardcore;
	}

	@Override
	public void undertale_death_animation$setButtonsActive(boolean value) {
		setButtonsActive(value);
	}
}