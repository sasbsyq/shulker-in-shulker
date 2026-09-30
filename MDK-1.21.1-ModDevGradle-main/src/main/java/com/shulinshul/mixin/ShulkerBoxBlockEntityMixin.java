package com.shulinshul.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.shulinshul.NestingHelper;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

/**
 * Mixin that enforces the nesting depth limit when a shulker box is inserted into another
 * shulker box by automation (hoppers, pipes, etc.).
 *
 * <p>Vanilla {@code ShulkerBoxBlockEntity#canPlaceItemThroughFace} explicitly rejects any
 * shulker box. We replace that blanket rejection with a depth-aware check so that nesting is
 * allowed up to the configured limit.</p>
 *
 * <p>We use {@code @ModifyReturnValue} so that our change is applied to the final return
 * value, even if other mods (such as hopper-overhaul mods) also modify this method. A high
 * priority ensures our modification runs last.</p>
 */
@Mixin(value = ShulkerBoxBlockEntity.class, priority = 1500)
public abstract class ShulkerBoxBlockEntityMixin {

    /**
     * Allows a shulker box to be inserted through a face when the resulting nesting depth is
     * within the configured limit.
     *
     * @param original  the value returned by the vanilla method (or by other mods)
     * @param index     the target slot index
     * @param itemStack the stack being inserted
     * @param direction the side the insertion comes from
     * @return {@code true} if the insertion is allowed
     */
    @ModifyReturnValue(method = "canPlaceItemThroughFace", at = @At("RETURN"))
    private boolean shul_in_shul$checkNestingDepth(boolean original, int index, ItemStack itemStack,
            @Nullable Direction direction) {
        if (!NestingHelper.isShulkerBox(itemStack)) {
            // Not a shulker box; leave the original result untouched.
            return original;
        }
        ShulkerBoxBlockEntity self = (ShulkerBoxBlockEntity) (Object) this;
        // If the block entity is not attached to a level yet, we cannot read the game rule;
        // in that case fall back to the original (vanilla) result.
        if (self.getLevel() == null) {
            return original;
        }
        return NestingHelper.canNest(self.getLevel(), self, itemStack);
    }
}
