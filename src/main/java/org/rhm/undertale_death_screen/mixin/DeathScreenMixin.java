package org.rhm.undertale_death_screen.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.rhm.undertale_death_screen.*;
import org.rhm.undertale_death_screen.registry.SoundEventRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*
dear readers,
I know this mixin is absolutely horrendous, and I hate it as well. I, however, am too lazy to rewrite it
 */
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen implements DeathScreenAccess {
    @Unique
    private static final List<Function<Player, Integer>> COLUMN_SELECTOR = List.of(
            (player) -> player.hasEffect(MobEffects.POISON) ? 1 : null,
            (player) -> player.hasEffect(MobEffects.WITHER) ? 2 : null,
            (player) -> player.isFreezing() ? 3 : null
    );

    @Unique
    private static final int HEART_WIDTH = 13;
    @Unique
    private static final int HEART_HEIGHT = 15;
    @Unique
    private static final int HEART_TEXTURE_WIDTH = HEART_WIDTH * 4;
    @Unique
    private static final int HEART_TEXTURE_HEIGHT = HEART_HEIGHT * 4;

    @Unique
    private static final ResourceLocation HEART_TEXTURE_LOCATION = UndertaleDeathScreenCommon.id("undertale_death/heart_shatter");
    @Unique
    private static final ResourceLocation HEART_TEXTURE_LOCATION_HC = UndertaleDeathScreenCommon.id("undertale_death/heart_shatter_hardcore");

    @Shadow
    @Final
    private boolean hardcore;

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
    private Integer undertale_death_animation$heartStyle;
    @Unique
    @Nullable
    private BGMSoundInstance undertale_death_animation$bgmSoundInstance;

    protected DeathScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderDeathBackground", at = @At("HEAD"), require = 0, cancellable = true)
    private static void disableBackground(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        ci.cancel();
    }

    @Shadow
    protected abstract void setButtonsActive(boolean bl);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setUpVariables(CallbackInfo ci) {
        if (Config.INSTANCE.getShouldStopSound()) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
            Minecraft.getInstance().getSoundManager().stop();
        }
        this.undertale_death_animation$age = 0;
        this.undertale_death_animation$finishedAge = 0;
        this.undertale_death_animation$bgmProgress = 0;
        this.undertale_death_animation$progress = 0;
        this.undertale_death_animation$pieces = new ArrayList<>();
        this.undertale_death_animation$hasFinished = false;
        this.undertale_death_animation$shouldStart = !Config.INSTANCE.getCenteredHeartAnimation() && Config.INSTANCE.getCenteredHeart();
        if (Minecraft.getInstance().player != null) {
            this.undertale_death_animation$randomSource = Minecraft.getInstance().player.level().getRandom();

            this.undertale_death_animation$heartStyle = 0;
            if (Config.INSTANCE.getDynamicHeart()) {
                for (Function<Player, Integer> selector : COLUMN_SELECTOR) {
                    Integer style = selector.apply(Minecraft.getInstance().player);
                    if (style != null) {
                        this.undertale_death_animation$heartStyle = style;
                        break;
                    }
                }
            }
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
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

        if (Config.INSTANCE.getCenteredHeart()) {
            x = (guiGraphics.guiWidth() / 2) - (HEART_WIDTH / 2);
            y = (guiGraphics.guiHeight() / 2) - (HEART_HEIGHT / 2);
        } else {
            x = (guiGraphics.guiWidth() / 2) - 91 - 2;
            y = guiGraphics.guiHeight() - 39 - 3;
        }

        double fadeSpeed = Config.INSTANCE.getBackgroundFadeSpeed();
        int bgColor = 0x00000000;

        if (fadeSpeed > 0) {
            if (fadeSpeed >= 1) {
                bgColor = 0xFF000000;
            } else {
                int alpha = (int) (
                        Mth.smoothstep(
                                Math.min((undertale_death_animation$age + delta) * Math.pow(fadeSpeed, 0.5) / 5, 1)
                        ) * 255
                );
                bgColor = (alpha << 24);
            }
        }

        if ((bgColor >>> 24) > 0) {
            guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), bgColor);
        }

        if (!undertale_death_animation$shouldStart) {
            undertale_death_animation$progress = Math.min(undertale_death_animation$progress + delta * Config.INSTANCE.getCenteredHeartSpeed(), 1);
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
                        x + HEART_WIDTH / 2f,
                        y + HEART_HEIGHT / 2f,
                        vx,
                        vy,
                        undertale_death_animation$randomSource.nextInt(HeartPiece.PIECE_TEXTURE_WIDTH / HeartPiece.PIECE_WIDTH),
                        undertale_death_animation$heartStyle * HeartPiece.PIECE_HEIGHT,
                        undertale_death_animation$randomSource.nextDouble() * 360,
                        undertale_death_animation$randomSource.nextDouble() * 8 - 2
                ));
            }
        } else { // Render the pieces
            if (undertale_death_animation$bgmProgress != -1 && undertale_death_animation$bgmProgress++ >= 35) {
                if (Config.INSTANCE.getDetermination()) {
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
                //? if >= 1.21.6 {
                /*net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                 *///?} else if >= 1.21.2
                /*net.minecraft.client.renderer.RenderType::guiTextured,*/
                this.hardcore ?
                        HEART_TEXTURE_LOCATION_HC : HEART_TEXTURE_LOCATION,
                HEART_TEXTURE_WIDTH,
                HEART_TEXTURE_HEIGHT,
                HEART_WIDTH * stage,
                HEART_HEIGHT * this.undertale_death_animation$heartStyle,
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
				HEART_HEIGHT * this.undertale_death_animation$heartStyle.column(),
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
    private void disableTint(GuiGraphics instance, int i, int j, int k, int l, int m, int n, Operation<Void> original) {
    }
}