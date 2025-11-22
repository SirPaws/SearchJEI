package dev.sirpaws.searchjei.query;

import net.minecraft.world.item.ItemStack;

public class GroupQuery extends Query {
    private final Query innerQuery;
    public GroupQuery(Query innerQuery) {
        this.innerQuery = innerQuery;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return innerQuery.matches(stack) == !negated;
    }
}
