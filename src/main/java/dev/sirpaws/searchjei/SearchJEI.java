package dev.sirpaws.searchjei;

import dev.sirpaws.searchjei.config.Config;
import dev.sirpaws.searchjei.utils.GuiUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchJEI implements ModInitializer {
	public static final String MOD_ID = "searchjei";

	public static final Config CONFIG = new Config();
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean JEI_LOADED = false;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		JEI_LOADED = FabricLoader.getInstance().isModLoaded("jei");

		LOGGER.info("Hello Fabric world!");
		GuiUtils.initUtil();
		GuiHandler.init();
	}
}