package com.shulinshul.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

/**
 * Accessor mixin exposing the {@code container} field of {@link Slot}.
 *
 * <p>The field is declared on {@code Slot} and inherited by {@code ShulkerBoxSlot}, so it
 * cannot be shadowed directly from a mixin targeting {@code ShulkerBoxSlot}. This accessor
 * lets us read it from the slot mixin.</p>
 */
@Mixin(Slot.class)
public interface SlotAccessor {

    /**
     * Returns the container backing the slot.
     *
     * @return the slot's container
     */
    @Accessor("container")
    Container shul_in_shul$getContainer();
}
