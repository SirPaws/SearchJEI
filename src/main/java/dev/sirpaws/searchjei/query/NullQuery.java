package dev.sirpaws.searchjei.query;

import net.minecraft.world.item.ItemStack;

public class NullQuery extends Query {
    public NullQuery() {}

    @Override
    public boolean matches(ItemStack stack) {
        return false;
    }
}
