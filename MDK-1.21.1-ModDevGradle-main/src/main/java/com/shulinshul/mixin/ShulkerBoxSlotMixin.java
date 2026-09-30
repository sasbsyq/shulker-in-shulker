package com.shulinshul.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.shulinshul.NestingHelper;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Mixin that enforces the nesting depth limit when a shulker box is placed into another
 * shulker box through its GUI.
 *
 * <p>Vanilla {@code ShulkerBoxSlot#mayPlace} simply delegates to
 * {@code ItemStack#canFitInsideContainerItems()}, which we have already relaxed in
 * {@code BlockItemMixin}. Here we add the depth check, using the slot's container (the
 * shulker box block entity) to determine the depth of the box being filled.</p>
 *
 * <p>We use {@code @ModifyReturnValue} so that our change is applied to the final return
 * value, even if other mods also modify this method. A high priority ensures our
 * modification runs last.</p>
 */
@Mixin(value = ShulkerBoxSlot.class, priority = 1500)
public abstract class ShulkerBoxSlotMixin {

    /**
     * Adds a nesting depth check on top of the vanilla placeability check.
     *
     * @param original the value returned by the vanilla method (or by other mods)
     * @param stack    the stack being placed into the slot
     * @return {@code true} if the stack may be placed
     */
    @ModifyReturnValue(method = "mayPlace", at = @At("RETURN"))
    private boolean shul_in_shul$checkNestingDepth(boolean original, ItemStack stack) {
        if (!NestingHelper.isShulkerBox(stack)) {
            // Not a shulker box; leave the original result untouched.
            return original;
        }
        // Read the backing container through the accessor mixin on Slot.
        Container container = ((SlotAccessor) this).shul_in_shul$getContainer();
        // The container is the shulker box block entity, which gives us the level so we can
        // read the per-world game rule.
        Level level = container instanceof BlockEntity blockEntity ? blockEntity.getLevel() : null;
        // Custom game rules are not synchronised to the client, and the client-side block
        // entity may not have a level at all. We therefore only enforce the depth check when
        // we have a server level; otherwise we allow the placement and let the server make
        // the final decision.
        if (level == null || level.isClientSide()) {
            return original;
        }
        return NestingHelper.canNest(level, container, stack);
    }
}
