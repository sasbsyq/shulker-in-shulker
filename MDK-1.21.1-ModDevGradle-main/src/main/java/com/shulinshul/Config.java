package com.shulinshul;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration for Shulker in Shulker.
 */
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /**
     * Fallback maximum nesting depth of shulker boxes.
     *
     * <p>The primary control is the per-world {@code shulkerMaxNestingDepth} game rule. This
     * config value is only used when no world is available to read the game rule from.</p>
     *
     * <p>A value of {@code 0} (the default) disables nesting entirely. A value of {@code 1}
     * allows a single layer (one shulker box inside another). Higher values allow deeper
     * nesting, which increases the resulting NBT data size.</p>
     */
    public static final ModConfigSpec.IntValue MAX_NESTING_DEPTH = BUILDER
            .comment("Fallback maximum number of nested shulker box layers allowed.",
                    "The per-world game rule 'shulkerMaxNestingDepth' takes priority;",
                    "this value is only used when no world is available.",
                    "0 = default, nesting is disabled.",
                    "1 = a shulker box may contain one layer of shulker boxes.",
                    "Higher values allow deeper nesting and increase NBT size.")
            .defineInRange("maxNestingDepth", 0, 0, 16);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
