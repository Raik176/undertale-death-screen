package org.rhm.undertale_death_screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3x2f;

public class HeartPiece {
    public static final ResourceLocation PIECES_TEXTURE_LOCATION = UndertaleDeathScreenCommon.id("undertale_death/heart_pieces");

    public static final int PIECE_TEXTURE_WIDTH = 40;
    public static final int PIECE_TEXTURE_HEIGHT = 20;

    public static final int PIECE_WIDTH = 5;
    public static final int PIECE_HEIGHT = 5;
    public static final int FRAME_DURATION = 4;
    public static final int TOTAL_FRAMES = PIECE_TEXTURE_WIDTH / PIECE_WIDTH;

    private final boolean animated;
    private final int textureX, textureY;
    private final double angularVelocity;
    public float x, y;
    private double vx, vy;
    private double rotation;
    private int currentFrame = 0;
    private int frameTickCounter = 0;

    public HeartPiece(float x, float y, double vx, double vy, int textureX, int textureY, double rotation, double angularVelocity) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.textureX = textureX;
        this.textureY = textureY;
        this.rotation = rotation;
        this.angularVelocity = angularVelocity;

        this.animated = Config.INSTANCE.getStyle() == Config.ShardRenderStyle.ANIMATED;
    }

    public void renderTick() {
        x += (float) vx;
        y += (float) vy;
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
        //? if >=1.21.6 {
        /*guiGraphics.pose().pushMatrix();
        *///?} else
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(
                x, y
                //? if <1.21.6
                ,0
        );

        if (!animated) {
            //? if >=1.21.6 {
            /*guiGraphics.pose().mul(new Matrix3x2f().rotation((float) Math.toRadians(rotation)));
            *///?} else
            guiGraphics.pose().mulPose(new org.joml.Quaternionf().rotateZ((float) Math.toRadians(rotation)));
            guiGraphics.pose().translate(
                    -PIECE_WIDTH / 2f, -PIECE_HEIGHT / 2f
                    //? if <1.21.6
                    ,0
            );
        }

        //? if >= 1.20.6 {
        guiGraphics.blit(
                //? if >= 1.21.6 {
                /*net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                *///?} else if >= 1.21.2
                /*net.minecraft.client.renderer.RenderType::guiTextured,*/
                PIECES_TEXTURE_LOCATION.withPrefix("textures/gui/sprites/").withSuffix(".png"),
                0,
                0,
                animated ? currentFrame * PIECE_WIDTH : textureX,
                textureY,
                PIECE_WIDTH,
                PIECE_HEIGHT,
                PIECE_TEXTURE_WIDTH,
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
                PIECE_HEIGHT,
                PIECE_TEXTURE_WIDTH,
                PIECE_TEXTURE_HEIGHT
        );
        *///?}

        //? if >=1.21.6 {
        /*guiGraphics.pose().popMatrix();
        *///?} else
        guiGraphics.pose().popPose();
    }
}