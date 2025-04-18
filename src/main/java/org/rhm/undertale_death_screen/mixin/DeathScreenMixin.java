package org.rhm.undertale_death_screen.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.rhm.undertale_death_screen.BGMSoundInstance;
import org.rhm.undertale_death_screen.DeathScreenAccess;
import org.rhm.undertale_death_screen.HeartPiece;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;
import org.rhm.undertale_death_screen.registry.SoundEventRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.2
/*import net.minecraft.client.renderer.RenderType;*/

/*
dear readers,
I know this mixin is absolutely horrendous, but im lazy
 */
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen implements DeathScreenAccess {
	@Shadow protected abstract void setButtonsActive(boolean bl);

	@Shadow @Final private boolean hardcore;
	@Unique
	private static final ResourceLocation HEART_TEXTURE_LOCATION = UndertaleDeathScreenCommon.id("undertale_death/heart_shatter");
	@Unique
	private static final ResourceLocation HEART_TEXTURE_LOCATION_HC = UndertaleDeathScreenCommon.id("undertale_death/heart_shatter_hardcore");

	@Unique
	private static final int HEART_WIDTH = 13;
	@Unique
	private static final int HEART_HEIGHT = 15;

	@Unique
	private static final int HEART_TEXTURE_WIDTH = HEART_WIDTH*4;
	@Unique
	private static final int HEART_TEXTURE_HEIGHT = HEART_HEIGHT;

	@Unique
	private RandomSource undertale_death_animation$randomSource;
	@Unique
	private List<HeartPiece> undertale_death_animation$pieces;
	@Unique
	private int undertale_death_animation$age;
	@Unique
	private int undertale_death_animation$finishedAge;
	@Unique
	private boolean undertale_death_animation$hasFinished;
	@Unique
	private boolean undertale_death_animation$shouldStart;
	@Unique
	private double undertale_death_animation$progress;

	@Unique
	private int undertale_death_animation$bgmProgress;
	@Unique
	@Nullable
	private BGMSoundInstance undertale_death_animation$bgmSoundInstance;

	protected DeathScreenMixin(Component component) {
		super(component);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void init(CallbackInfo ci) {
		if (UndertaleDeathScreenCommon.config.getShouldStopSound()) {
			Minecraft.getInstance().getMusicManager().stopPlaying();
			Minecraft.getInstance().getSoundManager().stop();
		}
		this.undertale_death_animation$age = 0;
		this.undertale_death_animation$finishedAge = 0;
		this.undertale_death_animation$bgmProgress = 0;
		this.undertale_death_animation$progress = 0;
		this.undertale_death_animation$pieces = new ArrayList<>();
		this.undertale_death_animation$hasFinished = false;
		this.undertale_death_animation$shouldStart = !UndertaleDeathScreenCommon.config.getCenteredHeartAnimation() && UndertaleDeathScreenCommon.config.getCenteredHeart();
		if (Minecraft.getInstance().player != null) // to stop idea from complaining, this should never actually not fire
			this.undertale_death_animation$randomSource = Minecraft.getInstance().player.level().getRandom();
		setButtonsActive(true);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo ci) {
		Minecraft.getInstance().getMusicManager().stopPlaying();

		if ((!undertale_death_animation$pieces.isEmpty()
				&& undertale_death_animation$pieces.stream().allMatch(piece -> piece.y >= this.height)) && !undertale_death_animation$hasFinished) {
			if (undertale_death_animation$finishedAge == 0) {
				undertale_death_animation$finishedAge = undertale_death_animation$age;
			} else if (undertale_death_animation$age - undertale_death_animation$finishedAge >= 5) {
				undertale_death_animation$pieces.clear();
				undertale_death_animation$hasFinished = true;
			}
		}
		this.undertale_death_animation$age++;
		if (this.undertale_death_animation$age == 25) {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEventRegistry.HEART_SHATTER_NOISES.get(), 1));
		}

		if (!undertale_death_animation$hasFinished)
			ci.cancel();
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(GuiGraphics guiGraphics, int i, int j, float delta, CallbackInfo ci) {
		int x;
		int y;

		if (UndertaleDeathScreenCommon.config.getCenteredHeart()) {
			x = (guiGraphics.guiWidth()/2) - (HEART_WIDTH/2);
			y = (guiGraphics.guiHeight()/2) - (HEART_HEIGHT/2);
		} else {
			x = (guiGraphics.guiWidth() / 2) - 91 - 2;
			y = guiGraphics.guiHeight() - 39 - 3;
		}

		guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFF000000);

		if (!undertale_death_animation$shouldStart) {
			undertale_death_animation$progress = Math.min(undertale_death_animation$progress + delta * UndertaleDeathScreenCommon.config.getCenteredHeartSpeed(), 1);
			double easedProgress = Mth.smoothstep(undertale_death_animation$progress);

			int startX = (guiGraphics.guiWidth() / 2) - 91 - 2;
			int startY = guiGraphics.guiHeight() - 39 - 3;

			int lerpedX = (int) Mth.lerp(easedProgress, startX, x);
			int lerpedY = (int) Mth.lerp(easedProgress, startY, y);

			undertale_death_animation$renderHeart(
					guiGraphics,
					0,
					lerpedX,
					lerpedY
			);

			if (lerpedX == x && lerpedY == y)
				undertale_death_animation$shouldStart = true;
		} else if (!undertale_death_animation$hasFinished && this.undertale_death_animation$age < 47) {
			undertale_death_animation$renderHeart(guiGraphics, (this.undertale_death_animation$age < 25) ? 0 : Math.min(3, this.undertale_death_animation$age - 25), x, y);
		} else if (!undertale_death_animation$hasFinished && undertale_death_animation$pieces.isEmpty()) {
			for (int i1 = 0; i1 < undertale_death_animation$randomSource.nextInt(6, 8); i1++) {
				double angle = undertale_death_animation$randomSource.nextDouble() * 2 * Math.PI;
				double speed = 2.0 + undertale_death_animation$randomSource.nextDouble() * 2.0;
				double vx = Math.cos(angle) * speed;
				double vy = Math.sin(angle) * speed;

				undertale_death_animation$pieces.add(new HeartPiece(
						x + HEART_WIDTH / 2.0,
						y + HEART_TEXTURE_HEIGHT / 2.0,
						vx,
						vy,
						undertale_death_animation$randomSource.nextInt(HeartPiece.PIECE_TEXTURE_WIDTH / HeartPiece.PIECE_WIDTH),
						0,
						undertale_death_animation$randomSource.nextDouble() * 360,
						undertale_death_animation$randomSource.nextDouble() * 8 - 2
				));
			}
		} else { // Render the pieces
			if (undertale_death_animation$bgmProgress != -1 && undertale_death_animation$bgmProgress++ >= 15) {
				if (UndertaleDeathScreenCommon.config.getDetermination()) {
					undertale_death_animation$bgmSoundInstance = new BGMSoundInstance(SoundEventRegistry.DETERMINATION.get());
					Minecraft.getInstance().getSoundManager().play(undertale_death_animation$bgmSoundInstance);
					undertale_death_animation$bgmSoundInstance.fadeIn();
				}
				undertale_death_animation$bgmProgress = -1;
			}

			for (HeartPiece piece : undertale_death_animation$pieces) {
				piece.renderTick();
				piece.render(guiGraphics);
			}
		}

		if (!undertale_death_animation$hasFinished)
			ci.cancel();
	}

	@Unique
	private void undertale_death_animation$renderHeart(GuiGraphics guiGraphics, int stage, int x, int y) {
		//? if >=1.20.6 {
        guiGraphics.blitSprite(
                //? if >=1.21.2
                /*RenderType::guiTextured,*/
                this.hardcore ?
                        HEART_TEXTURE_LOCATION_HC : HEART_TEXTURE_LOCATION,
                HEART_TEXTURE_WIDTH,
                HEART_TEXTURE_HEIGHT,
                HEART_WIDTH * stage,
                0,
                x,
                y,
                HEART_WIDTH,
                HEART_HEIGHT
        );
        //?} else {
		/*guiGraphics.blit(
				(this.hardcore ?
						HEART_TEXTURE_LOCATION_HC : HEART_TEXTURE_LOCATION).withPrefix("textures/gui/sprites/").withSuffix(".png"),
				x,
				y,
				HEART_WIDTH * stage,
				0,
				HEART_WIDTH,
				HEART_HEIGHT,
				HEART_TEXTURE_WIDTH,
				HEART_TEXTURE_HEIGHT
		);
		*///?}
	}

	@Override
	public void undertale_death_animation$stopBackgroundMusic() {
		Minecraft.getInstance().getSoundManager().stop(undertale_death_animation$bgmSoundInstance);
	}

	// me when I ignore mixin standard
	@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"
			),
			require = 0
	)
	private void disableTint(GuiGraphics instance, int i, int j, int k, int l, int m, int n, Operation<Void> original) { }

	@Inject(method = "renderDeathBackground", at = @At("HEAD"), require = 0, cancellable = true)
	private static void disableBackground(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
		ci.cancel();
	}
}