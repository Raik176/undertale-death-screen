package org.rhm.undertale_death_screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

//? if >=1.21.2
/*import net.minecraft.client.renderer.RenderType;*/

public class HeartPiece {
    public static final ResourceLocation PIECES_TEXTURE_LOCATION = UndertaleDeathScreenCommon.id("undertale_death/heart_pieces");

    public static final int PIECE_TEXTURE_WIDTH = 40;
    public static final int PIECE_TEXTURE_HEIGHT = 5;

    public static final int PIECE_WIDTH = 5;
    public static final int FRAME_DURATION = 4;
    public static final int TOTAL_FRAMES = PIECE_TEXTURE_WIDTH / PIECE_WIDTH;

    private final boolean animated;

    public double x, y;
    private double vx, vy;
    private final int textureX, textureY;
    private double rotation;
    private final double angularVelocity;

    private int currentFrame = 0;
    private int frameTickCounter = 0;

    public HeartPiece(double x, double y, double vx, double vy, int textureX, int textureY, double rotation, double angularVelocity) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.textureX = textureX;
        this.textureY = textureY;
        this.rotation = rotation;
        this.angularVelocity = angularVelocity;

        this.animated = UndertaleDeathScreenCommon.config.getStyle() == Config.ShardRenderStyle.ANIMATED;
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

        if (animated) {
            frameTickCounter++;
            if (frameTickCounter >= FRAME_DURATION) {
                frameTickCounter = 0;
                currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
            }
        }
    }

    public void render(GuiGraphics guiGraphics) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        if (!animated) {
            guiGraphics.pose().mulPose(new Quaternionf().rotateZ((float) Math.toRadians(rotation)));
            guiGraphics.pose().translate(-PIECE_WIDTH / 2.0, -PIECE_TEXTURE_HEIGHT / 2.0, 0);
        }

        //? if >= 1.20.6 {
            guiGraphics.blitSprite(
                    //? if >=1.21.2
                    /*RenderType::guiTextured,*/
                    PIECES_TEXTURE_LOCATION,
                    PIECE_TEXTURE_WIDTH,
                    PIECE_TEXTURE_HEIGHT,
                    animated ? currentFrame * PIECE_WIDTH : textureX,
                    textureY,
                    0,
                    0,
                    PIECE_WIDTH,
                    PIECE_TEXTURE_HEIGHT
            );
            //?} else {
        /*guiGraphics.blit(
                PIECES_TEXTURE_LOCATION.withPrefix("textures/gui/sprites/").withSuffix(".png"),
                0,
                0,
                animated ? currentFrame * PIECE_WIDTH : textureX,
                textureY,
                PIECE_WIDTH,
                PIECE_TEXTURE_HEIGHT,
                PIECE_TEXTURE_WIDTH,
                PIECE_TEXTURE_HEIGHT
        );
        *///?}

        guiGraphics.pose().popPose();
    }
}