package org.rhm.undertale_death_screen.compat.impl;

import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.rhm.undertale_death_screen.Config;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;
import org.rhm.undertale_death_screen.compat.ClothConfigCompatBase;

public class ClothConfigCompat implements ClothConfigCompatBase {
    @SuppressWarnings("UnstableApiUsage")
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
                .setSaveConsumer((value) -> UndertaleDeathScreenCommon.config.setStyle(value))
                .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                UndertaleDeathScreenCommon.translatable("config.music_turnoff"),
                UndertaleDeathScreenCommon.config.getShouldStopSound()
            ).setDefaultValue(Config.DEFAULT_MUSIC_TURNOFF)
            .setTooltip(UndertaleDeathScreenCommon.translatable("config.music_turnoff.ttp"))
            .setSaveConsumer((value) -> UndertaleDeathScreenCommon.config.setShouldStopSound(value))
            .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.determination"),
                        UndertaleDeathScreenCommon.config.getDetermination()
                ).setDefaultValue(Config.DEFAULT_DETERMINATION)
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.determination.ttp"))
                .setSaveConsumer((value) -> UndertaleDeathScreenCommon.config.setDetermination(value))
                .build());
        BooleanListEntry centerHeartToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.centered_heart"),
                        UndertaleDeathScreenCommon.config.getCenteredHeart()
                ).setDefaultValue(Config.DEFAULT_CENTERED_HEART)
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart.ttp"))
                .setSaveConsumer((value) -> UndertaleDeathScreenCommon.config.setCenteredHeart(value))
                .build();
        general.addEntry(centerHeartToggle);
        BooleanListEntry centerHeartAnimationToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.centered_heart_anim"),
                        UndertaleDeathScreenCommon.config.getCenteredHeartAnimation()
                ).setDefaultValue(Config.DEFAULT_CENTERED_HEART_ANIMATION)
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart_anim.ttp"))
                .setSaveConsumer((value) -> UndertaleDeathScreenCommon.config.setCenteredHeartAnimation(value))
                .setDisplayRequirement(Requirement.isTrue(centerHeartToggle))
                .build();
        general.addEntry(centerHeartAnimationToggle);
        general.addEntry(entryBuilder.startDoubleField(
                                UndertaleDeathScreenCommon.translatable("config.centered_heart_speed"),
                                UndertaleDeathScreenCommon.config.getCenteredHeartSpeed()
                        ).setDefaultValue(Config.DEFAULT_CENTERED_HEART_SPEED)
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart_speed.ttp"))
                        .setSaveConsumer((value) -> UndertaleDeathScreenCommon.config.setCenteredHeartSpeed(value))
                        .setDisplayRequirement(Requirement.all(Requirement.isTrue(centerHeartAnimationToggle), Requirement.isTrue(centerHeartToggle)))
                        .build()
        );

        return builder.build();
    }
}
