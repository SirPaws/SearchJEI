package dev.sirpaws.searchjei.query;

import net.minecraft.world.item.ItemStack;

public abstract class Query {
    public boolean negated;

    public abstract boolean matches(ItemStack stack);
}
