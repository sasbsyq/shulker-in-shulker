package com.shulinshul.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.ShulkerBoxBlock;

/**
 * Mixin that removes the blanket restriction preventing shulker boxes from being stored
 * inside container items.
 *
 * <p>Vanilla {@code BlockItem#canFitInsideContainerItems()} returns {@code false} for any
 * shulker box, which is the root cause of shulker boxes being unable to hold other shulker
 * boxes. We override it to return {@code true} for shulker boxes; the actual depth limit is
 * enforced separately where the containing inventory is known (see {@code ShulkerBoxSlotMixin}
 * and {@code ShulkerBoxBlockEntityMixin}).</p>
 *
 * <p>We use {@code @ModifyReturnValue} rather than a cancellable {@code @Inject} so that our
 * change is applied to the final return value, even if other mods also modify this method.
 * A high priority ensures our modification runs last.</p>
 */
@Mixin(value = BlockItem.class, priority = 1500)
public abstract class BlockItemMixin {

    /**
     * Allows shulker boxes to fit inside container items.
     *
     * @param original the value returned by the vanilla method (or by other mods)
     * @return {@code true} for shulker boxes, otherwise the original value
     */
    @ModifyReturnValue(method = "canFitInsideContainerItems", at = @At("RETURN"))
    private boolean shul_in_shul$allowShulkerInContainer(boolean original) {
        BlockItem self = (BlockItem) (Object) this;
        if (self.getBlock() instanceof ShulkerBoxBlock) {
            return true;
        }
        return original;
    }
}
