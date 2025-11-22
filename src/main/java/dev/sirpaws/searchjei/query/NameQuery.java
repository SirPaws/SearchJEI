package dev.sirpaws.searchjei.query;

import net.minecraft.world.item.ItemStack;

public class NameQuery extends Query {
    private final String name;

    public NameQuery(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean matches(ItemStack stack) {
        var name = stack.getItemName();
        var text = name.getString();
        return text.toLowerCase().contains(this.name) == !negated;
    }
}
