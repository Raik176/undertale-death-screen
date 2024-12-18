package org.rhm.undertale_death_screen;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Config {
    public static final ShardRenderStyle DEFAULT_STYLE = ShardRenderStyle.ROTATION;
    public static final boolean DEFAULT_MUSIC_TURNOFF = true;

    private ShardRenderStyle style;
    private boolean musicTurnoff;

    public static final Codec<Config> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    ShardRenderStyle.CODEC.optionalFieldOf("shardRenderStyle", DEFAULT_STYLE).forGetter(Config::getStyle),
                    Codec.BOOL.optionalFieldOf("musicTurnoff", DEFAULT_MUSIC_TURNOFF).forGetter(Config::getShouldStopSound)
            ).apply(instance, Config::new));

    public Config(ShardRenderStyle style, boolean musicTurnoff) {
        this.style = style;
    }

    public Config() {
        this.style = DEFAULT_STYLE;
        this.musicTurnoff = DEFAULT_MUSIC_TURNOFF;
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

    public void save(File location) {
        DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, this);
        if (result.error().isPresent() || result.isError() || result.result().isEmpty()) {
            UndertaleDeathScreenCommon.logger.warn("Failed to encode the configuration. Configuration not saved.");
            result.error().ifPresent(error -> UndertaleDeathScreenCommon.logger.error("Error details: {}", error.message()));
        } else {
            try {
                Files.writeString(
                        location.toPath(),
                        new GsonBuilder().setPrettyPrinting().create().toJson(result.result().get())
                );
            } catch (IOException exception) {
                UndertaleDeathScreenCommon.logger.error("IOException occurred while saving configuration file.", exception);
                UndertaleDeathScreenCommon.logger.warn("Failed to save configuration. Configuration not saved.");
            }
        }
    }

    public enum ShardRenderStyle implements StringRepresentable {
        ANIMATED("animated"),
        ROTATION("rotated");

        public static final Codec<ShardRenderStyle> CODEC = StringRepresentable.fromEnum(ShardRenderStyle::values);
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
