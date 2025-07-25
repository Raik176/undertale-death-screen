package org.rhm.undertale_death_screen.compat.impl;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
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
                .setTitle(Component.literal(UndertaleDeathScreenCommon.MOD_NAME))
                .setSavingRunnable(Config.INSTANCE::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.empty()); // doesn't show when there's only 1 category anyway
        general.addEntry(entryBuilder.startEnumSelector(
                                UndertaleDeathScreenCommon.translatable("config.shard_render_style"),
                                Config.ShardRenderStyle.class,
                                Config.INSTANCE.getStyle()
                        ).setDefaultValue(Config.getDefault().getStyle())
                        .setEnumNameProvider((e) -> UndertaleDeathScreenCommon.translatable(
                                "config.shard_render_style." + ((Config.ShardRenderStyle) e).getSerializedName())
                        )
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.shard_render_style.ttp"))
                        .setSaveConsumer(Config.INSTANCE::setStyle)
                        .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                                UndertaleDeathScreenCommon.translatable("config.music_turnoff"),
                                Config.INSTANCE.getShouldStopSound()
                        ).setDefaultValue(Config.getDefault().getShouldStopSound())
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.music_turnoff.ttp"))
                        .setSaveConsumer(Config.INSTANCE::setShouldStopSound)
                        .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.determination"),
                        Config.INSTANCE.getDetermination()
                ).setDefaultValue(Config.getDefault().getDetermination())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.determination.ttp"))
                .setSaveConsumer(Config.INSTANCE::setDetermination)
                .build());
        BooleanListEntry centerHeartToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.centered_heart"),
                        Config.INSTANCE.getCenteredHeart()
                ).setDefaultValue(Config.getDefault().getCenteredHeart())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart.ttp"))
                .setSaveConsumer(Config.INSTANCE::setCenteredHeart)
                .build();
        general.addEntry(centerHeartToggle);
        BooleanListEntry centerHeartAnimationToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.centered_heart_anim"),
                        Config.INSTANCE.getCenteredHeartAnimation()
                ).setDefaultValue(Config.getDefault().getCenteredHeartAnimation())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart_anim.ttp"))
                .setSaveConsumer(Config.INSTANCE::setCenteredHeartAnimation)
                .setDisplayRequirement(Requirement.isTrue(centerHeartToggle))
                .build();
        general.addEntry(centerHeartAnimationToggle);
        general.addEntry(entryBuilder.startDoubleField(
                                UndertaleDeathScreenCommon.translatable("config.centered_heart_speed"),
                                Config.INSTANCE.getCenteredHeartSpeed()
                        ).setDefaultValue(Config.getDefault().getCenteredHeartSpeed())
                        .setMin(0.1)
                        .setMax(1)
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart_speed.ttp"))
                        .setSaveConsumer(Config.INSTANCE::setCenteredHeartSpeed)
                        .setDisplayRequirement(Requirement.all(Requirement.isTrue(centerHeartAnimationToggle), Requirement.isTrue(centerHeartToggle)))
                        .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.dynamicHeart"),
                        Config.INSTANCE.getDynamicHeart()
                ).setDefaultValue(Config.getDefault().getDynamicHeart())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.dynamicHeart.ttp"))
                .setSaveConsumer(Config.INSTANCE::setDynamicHeart)
                .build());

        return builder.build();
    }
}
