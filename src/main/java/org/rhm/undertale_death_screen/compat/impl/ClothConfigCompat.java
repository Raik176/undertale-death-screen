package org.rhm.undertale_death_screen.compat.impl;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.rhm.undertale_death_screen.Config;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;
import org.rhm.undertale_death_screen.compat.ClothConfigCompatBase;

public class ClothConfigCompat implements ClothConfigCompatBase {
    @Override
    public Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable(""))
                .setSavingRunnable(() -> UndertaleDeathScreenCommon.config.save(UndertaleDeathScreenCommon.configFile));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.empty()); // doesn't show when there's only 1 category anyway
        general.addEntry(entryBuilder.startEnumSelector(
                    UndertaleDeathScreenCommon.translatable("config.shard_render_style"),
                    Config.ShardRenderStyle.class,
                    UndertaleDeathScreenCommon.config.getStyle()
                ).setDefaultValue(Config.DEFAULT_STYLE)
                .setEnumNameProvider((e) -> UndertaleDeathScreenCommon.translatable(
                        "config.shard_render_style." + ((Config.ShardRenderStyle)e).getSerializedName())
                )
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.shard_render_style.ttp"))
                .setSaveConsumer((style) -> UndertaleDeathScreenCommon.config.setStyle(style))
                .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                UndertaleDeathScreenCommon.translatable("config.music_turnoff"),
                UndertaleDeathScreenCommon.config.getShouldStopSound()
            ).setDefaultValue(Config.DEFAULT_MUSIC_TURNOFF)
            .setTooltip(UndertaleDeathScreenCommon.translatable("config.music_turnoff.ttp"))
            .setSaveConsumer((musicTurnoff) -> UndertaleDeathScreenCommon.config.setShouldStopSound(musicTurnoff))
            .build()
        );

        return builder.build();
    }
}
