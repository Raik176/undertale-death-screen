package org.rhm.undertale_death_screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = UndertaleDeathScreenCommon.impl.getConfigDir().resolve(UndertaleDeathScreenCommon.MOD_ID + ".json");

    public static final Config INSTANCE = load();

    private ShardRenderStyle style = ShardRenderStyle.ROTATION;
    private boolean musicTurnoff = true;
    private boolean determination = true;
    private boolean centeredHeart = true;
    private boolean centeredHeartAnimation = false;
    private double centeredHeartSpeed = 0.15;
    private boolean dynamicHeart = true;

    private Config() {
    }

    public static Config getDefault() {
        return new Config();
    }

    private static Config load() {
        Config config = null;
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                    config = GSON.fromJson(reader, Config.class);
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            UndertaleDeathScreenCommon.logger.error("Failed to load configuration file. Using default values.", e);
        }

        if (config == null) {
            config = getDefault();
        }

        config.centeredHeartSpeed = Math.max(0.1, Math.min(1.0, config.centeredHeartSpeed));
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            UndertaleDeathScreenCommon.logger.error("Failed to save configuration file:", e);
        }
    }

    public boolean getDynamicHeart() {
        return dynamicHeart;
    }

    public void setDynamicHeart(boolean value) {
        dynamicHeart = value;
    }

    public boolean getDetermination() {
        return determination;
    }

    public void setDetermination(boolean value) {
        determination = value;
    }

    public double getCenteredHeartSpeed() {
        return centeredHeartSpeed;
    }

    public void setCenteredHeartSpeed(double value) {
        centeredHeartSpeed = value;
    }

    public boolean getCenteredHeartAnimation() {
        return centeredHeartAnimation;
    }

    public void setCenteredHeartAnimation(boolean value) {
        centeredHeartAnimation = value;
    }

    public boolean getCenteredHeart() {
        return centeredHeart;
    }

    public void setCenteredHeart(boolean value) {
        centeredHeart = value;
    }

    public ShardRenderStyle getStyle() {
        return style;
    }

    public void setStyle(ShardRenderStyle style) {
        this.style = style;
    }

    public boolean getShouldStopSound() {
        return musicTurnoff;
    }

    public void setShouldStopSound(boolean musicTurnoff) {
        this.musicTurnoff = musicTurnoff;
    }

    public enum ShardRenderStyle implements StringRepresentable {
        ANIMATED("animated"),
        ROTATION("rotated");

        private final String name;

        ShardRenderStyle(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}