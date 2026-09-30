package com.shulinshul;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

/**
 * Utility methods for computing and limiting the nesting depth of shulker boxes.
 *
 * <p>The nesting depth of a shulker box is derived purely from its contents, so no custom
 * data needs to be stored on the item or block entity. This avoids any risk of corrupting
 * or bloating saved data.</p>
 *
 * <p>Depth is defined recursively:</p>
 * <ul>
 *     <li>A shulker box that contains no shulker boxes has depth {@code 0}.</li>
 *     <li>A shulker box has depth {@code 1 + max(depth of each shulker box it contains)}.</li>
 * </ul>
 *
 * <p>The maximum allowed depth is controlled by the {@code shulkerMaxNestingDepth} game
 * rule, which is stored per-world. When no world is available, the value from the mod's
 * common config is used as a fallback.</p>
 *
 * <p>Shulker boxes are recognised by the presence of the {@code CONTAINER} data component,
 * so this works for vanilla shulker boxes as well as those added by other mods.</p>
 */
public final class NestingHelper {
    private NestingHelper() {
    }

    /**
     * Returns whether the given stack is a shulker box item.
     *
     * <p>A shulker box is identified by the presence of the {@code CONTAINER} data component,
     * which is used by vanilla shulker boxes (and by most modded shulker boxes) to store
     * their contents. This is more general than checking for {@code ShulkerBoxBlock}, so it
     * also recognises shulker boxes added by other mods.</p>
     *
     * <p>Note that chests and barrels do not carry this component (their contents live in a
     * block entity), so they are not treated as shulker boxes. They can still be placed
     * inside shulker boxes, as in vanilla.</p>
     *
     * @param stack the stack to test
     * @return {@code true} if the stack is a shulker box
     */
    public static boolean isShulkerBox(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.CONTAINER);
    }

    /**
     * Returns the maximum nesting depth allowed in the given level.
     *
     * <p>This reads the {@code shulkerMaxNestingDepth} game rule from the level. If the
     * level is {@code null} (for example during certain client-side or early-loading
     * situations), the value from the common config is used instead.</p>
     *
     * @param level the level to read the game rule from, or {@code null}
     * @return the maximum nesting depth
     */
    public static int getMaxDepth(Level level) {
        if (level != null) {
            return level.getGameRules().getInt(ModGameRules.SHULKER_MAX_NESTING_DEPTH);
        }
        return Config.MAX_NESTING_DEPTH.getAsInt();
    }

    /**
     * Computes the nesting depth of the given shulker box stack from its contents.
     *
     * @param stack the shulker box stack to inspect
     * @return the nesting depth, or {@code 0} if the stack is not a shulker box or is empty
     */
    public static int getDepth(ItemStack stack) {
        if (!isShulkerBox(stack)) {
            return 0;
        }
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents == null) {
            return 0;
        }
                int maxChildDepth = 0;
        for (ItemStack child : contents.nonEmptyItems()) {
            if (isShulkerBox(child)) {
                maxChildDepth = Math.max(maxChildDepth, getDepth(child));
            }
        }
        return maxChildDepth + 1;
    }

    /**
     * Computes the nesting depth of a shulker box whose contents are held in the given
     * container (for example a placed shulker box block entity).
     *
     * @param container the container holding the shulker box's contents
     * @return the nesting depth of the box, or {@code 0} if it contains no shulker boxes
     */
    public static int getDepth(Container container) {
        int maxChildDepth = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack child = container.getItem(i);
            if (isShulkerBox(child)) {
                maxChildDepth = Math.max(maxChildDepth, getDepth(child));
            }
        }
        return maxChildDepth + 1;
    }

    /**
     * Determines whether {@code inner} may be placed inside {@code outer}.
     *
     * <p>After insertion, {@code outer} would have depth
     * {@code 1 + max(depth(inner), currentDepth(outer) - 1)}. Placement is allowed only when
     * that resulting depth does not exceed the maximum for the given level.</p>
     *
     * @param level the level the placement happens in, used to read the game rule
     * @param outer the shulker box that would contain {@code inner}
     * @param inner the shulker box that would be placed inside {@code outer}
     * @return {@code true} if the placement is allowed
     */
    public static boolean canNest(Level level, ItemStack outer, ItemStack inner) {
        if (!isShulkerBox(outer) || !isShulkerBox(inner)) {
            return true;
        }
        int resultingDepth = Math.max(getDepth(inner), getDepth(outer) - 1) + 1;
        return resultingDepth <= getMaxDepth(level);
    }

    /**
     * Determines whether {@code inner} may be placed inside the shulker box backed by the
     * given container.
     *
     * @param level     the level the placement happens in, used to read the game rule
     * @param container the container holding the target shulker box's contents
     * @param inner     the shulker box that would be placed inside
     * @return {@code true} if the placement is allowed
     */
    public static boolean canNest(Level level, Container container, ItemStack inner) {
        if (!isShulkerBox(inner)) {
            return true;
        }
        int resultingDepth = Math.max(getDepth(inner), getDepth(container) - 1) + 1;
        return resultingDepth <= getMaxDepth(level);
    }
}
