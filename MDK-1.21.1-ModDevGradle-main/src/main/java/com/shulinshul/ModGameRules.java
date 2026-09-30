package com.shulinshul;

import net.minecraft.world.level.GameRules;

/**
 * Registers the custom game rules added by Shulker in Shulker.
 *
 * <p>Game rules are stored per-world, which lets players change the maximum nesting depth
 * for each save independently using the vanilla {@code /gamerule} command.</p>
 */
public final class ModGameRules {
    /**
     * The maximum number of nested shulker box layers allowed in a world.
     *
     * <p>A value of {@code 0} (the default) disables nesting entirely. A value of {@code 1}
     * allows a single layer (one shulker box inside another). Players who want deeper
     * nesting can raise it with {@code /gamerule shulkerMaxNestingDepth <value>}.</p>
     */
    public static final GameRules.Key<GameRules.IntegerValue> SHULKER_MAX_NESTING_DEPTH =
            GameRules.register(
                    "shulkerMaxNestingDepth",
                    GameRules.Category.MISC,
                    GameRules.IntegerValue.create(0));

    private ModGameRules() {
    }
}
