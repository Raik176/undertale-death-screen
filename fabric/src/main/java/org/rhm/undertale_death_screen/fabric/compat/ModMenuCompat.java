package org.rhm.undertale_death_screen.fabric.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import org.rhm.undertale_death_screen.UndertaleDeathScreenCommon;

@Entrypoint("modmenu")
public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return UndertaleDeathScreenCommon::getConfigScreen;
    }
}
