package com.example.template.fabric;

import net.fabricmc.api.ModInitializer;
import com.example.template.TemplateModCommon;

public class TemplateModFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		TemplateModCommon.init();
	}
}
