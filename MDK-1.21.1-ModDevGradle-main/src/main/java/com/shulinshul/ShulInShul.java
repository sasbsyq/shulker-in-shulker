package com.shulinshul;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Main mod class for Shulker in Shulker.
 *
 * <p>This mod allows shulker boxes to be placed inside other shulker boxes, up to a
 * configurable maximum nesting depth (default 3). The behaviour is implemented through
 * Mixins that relax the vanilla restrictions in {@code BlockItem},
 * {@code ShulkerBoxSlot} and {@code ShulkerBoxBlockEntity}, combined with a depth check
 * that reads the per-world {@code shulkerMaxNestingDepth} game rule.</p>
 */
@Mod(ShulInShul.MODID)
public class ShulInShul {
    /** The mod id, must match the value in gradle.properties and neoforge.mods.toml. */
    public static final String MODID = "shul_in_shul";

    /** Shared logger instance. */
    public static final Logger LOGGER = LogUtils.getLogger();

    static {
        // Register our custom game rule as early as possible. Game rules must be registered
        // before any GameRules instance (i.e. any world) is created, otherwise the rule will
        // not appear in that world. Referencing the field here forces ModGameRules to load,
        // which performs the registration in its static initializer.
        LOGGER.info("Registering game rule: {}", ModGameRules.SHULKER_MAX_NESTING_DEPTH.getId());
    }

    public ShulInShul(IEventBus modEventBus, ModContainer modContainer) {
        // Register our config so that FML creates and loads it for us.
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
