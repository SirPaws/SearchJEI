package dev.sirpaws.searchjei.query;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import static dev.sirpaws.searchjei.utils.ItemUtils.getModName;

public class ModQuery extends Query {
    private final String name;

    public ModQuery(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean matches(ItemStack stack) {
        String modName = getModName(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
        return modName.toLowerCase().contains(name);
    }
}
