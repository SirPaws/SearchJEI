package dev.sirpaws.searchjei.query;

import net.minecraft.world.item.ItemStack;

public class AndQuery extends Query {
    private final Query left;
    private final Query right;

    public AndQuery(Query left, Query right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return (this.left.matches(stack) && this.right.matches(stack)) == !negated;
    }
}
