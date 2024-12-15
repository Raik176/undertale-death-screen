package com.example.template.forge;

import com.example.template.TemplateModCommon;
import net.minecraftforge.fml.common.Mod;

@Mod(TemplateModCommon.MOD_ID)
public class TemplateModForge {
	public TemplateModForge() {
		TemplateModCommon.init();
	}
}
