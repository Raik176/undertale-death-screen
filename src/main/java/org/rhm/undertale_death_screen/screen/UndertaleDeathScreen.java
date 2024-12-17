package org.rhm.undertale_death_screen.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.rhm.undertale_death_screen.DeathScreenAccess;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;
import org.rhm.undertale_death_screen.registry.SoundEventRegistry;

import java.util.ArrayList;
import java.util.List;


//?if >=1.21.2
import net.minecraft.client.renderer.RenderType;

public class UndertaleDeathScreen extends Screen {
    private static final ResourceLocation HEART_TEXTURE_LOCATION = UndertaleDeathScreenCommon.id("undertale_death/heart_shatter");
    private static final ResourceLocation HEART_TEXTURE_LOCATION_HC = UndertaleDeathScreenCommon.id("undertale_death/heart_shatter_hardcore");

    private static final int HEART_TEXTURE_WIDTH = 52;
    private static final int HEART_TEXTURE_HEIGHT = 15;

    private static final int HEART_WIDTH = 13;


    private final DeathScreen originalScreen;
    private final DeathScreenAccess originalAccess;
    private RandomSource randomSource;
    private List<HeartPiece> pieces;
    private int age;
    private int finishedAge;
    private boolean hasFinished;

    public UndertaleDeathScreen(DeathScreen originalScreen) {
        super(originalScreen.getTitle());
        this.originalScreen = originalScreen;
        this.originalAccess = (DeathScreenAccess) originalScreen;
        this.originalAccess.undertale_death_animation$setButtonsActive(true);
    }

    @Override
    protected void init() {
        this.age = 0;
        this.finishedAge = 0;
        this.pieces = new ArrayList<>();
        this.hasFinished = false;
        if (Minecraft.getInstance().player != null) // to stop idea from complaining, this should never actually not fire
            this.randomSource = Minecraft.getInstance().player.getRandom();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        // love the random magic numbers
        int x = (guiGraphics.guiWidth() / 2) - 91 - 2; // -2 because original heart is 9x9 but this one's 13x13
        int y = guiGraphics.guiHeight() - 39 - 3; // for some reason needs to be -3 here

        if (hasFinished)
            originalScreen.render(guiGraphics, i, j, f);
        else if (this.age < 47) {
            if (this.age == 25) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEventRegistry.HEART_SHATTER_NOISES.get(), 1));
            }
            int stage = (this.age < 25) ? 0 : Math.min(3, this.age - 25);
            guiGraphics.blitSprite(
                    //?if >=1.21.2
                    RenderType::guiTextured,
                    originalAccess.undertale_death_animation$isHardcore() ?
                            HEART_TEXTURE_LOCATION_HC : HEART_TEXTURE_LOCATION,
                    HEART_TEXTURE_WIDTH,
                    HEART_TEXTURE_HEIGHT,
                    HEART_WIDTH * stage, 0,
                    x, y,
                    HEART_WIDTH,
                    HEART_TEXTURE_HEIGHT
            );
        } else if (pieces.isEmpty()) {
            for (int i1 = 0; i1 < randomSource.nextInt(6, 8); i1++) {
                double angle = randomSource.nextDouble() * 2 * Math.PI;
                double speed = 2.0 + randomSource.nextDouble() * 2.0;
                double vx = Math.cos(angle) * speed;
                double vy = Math.sin(angle) * speed;

                pieces.add(new HeartPiece(
                        x + HEART_WIDTH / 2.0,
                        y + HEART_TEXTURE_HEIGHT / 2.0,
                        vx,
                        vy,
                        randomSource.nextInt(HeartPiece.PIECE_TEXTURE_WIDTH / HeartPiece.PIECE_WIDTH),
                        0,
                        randomSource.nextDouble() * 360,
                        randomSource.nextDouble() * 4 - 2
                ));
            }
        } else { // Render the pieces
            for (HeartPiece piece : pieces) {
                piece.renderTick();
                piece.render(guiGraphics);
            }
        }
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (hasFinished)
            return originalScreen.mouseClicked(d,e,i);
        return super.mouseClicked(d,e,i);
    }

    @Override
    public void tick() {
        if ((!pieces.isEmpty() && pieces.stream().allMatch(piece -> piece.y >= this.height)) && !hasFinished) {
            if (finishedAge == 0) {
                finishedAge = age;
            } else if (age - finishedAge >= 5) {
                pieces.clear();
                hasFinished = true;
            }
        }
        this.age++;
        super.tick();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        originalScreen.renderBackground(guiGraphics, i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class HeartPiece {
        private static final ResourceLocation PIECES_TEXTURE_LOCATION = UndertaleDeathScreenCommon.id("undertale_death/heart_pieces");

        private static final int PIECE_TEXTURE_WIDTH = 40;
        private static final int PIECE_TEXTURE_HEIGHT = 5;

        private static final int PIECE_WIDTH = 5;

        private double x, y;
        private double vx, vy;
        private final int textureX, textureY;
        private double rotation;
        private final double angularVelocity;

        public HeartPiece(double x, double y, double vx, double vy, int textureX, int textureY, double rotation, double angularVelocity) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.textureX = textureX;
            this.textureY = textureY;
            this.rotation = rotation;
            this.angularVelocity = angularVelocity;
        }

        public void renderTick() {
            x += vx;
            y += vy;
            vy += 0.1;
            vx *= 0.98;
            vy *= 0.98;

            rotation += angularVelocity;
            if (rotation >= 360) {
                rotation -= 360;
            } else if (rotation < 0) {
                rotation += 360;
            }
        }

        public void render(GuiGraphics guiGraphics) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x, y, 0);
            guiGraphics.pose().mulPose(new Quaternionf().rotateZ((float) Math.toRadians(rotation)));
            guiGraphics.pose().translate(-PIECE_WIDTH / 2.0, -PIECE_TEXTURE_HEIGHT / 2.0, 0);

            guiGraphics.blitSprite(
                    //?if >=1.21.2
                    RenderType::guiTextured,
                    PIECES_TEXTURE_LOCATION,
                    PIECE_TEXTURE_WIDTH,
                    PIECE_TEXTURE_HEIGHT,
                    textureX,
                    textureY,
                    0,
                    0,
                    PIECE_WIDTH,
                    PIECE_TEXTURE_HEIGHT
            );

            guiGraphics.pose().popPose();
        }
    }
}