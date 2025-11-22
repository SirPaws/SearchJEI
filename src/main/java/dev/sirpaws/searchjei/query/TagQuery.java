package dev.sirpaws.searchjei.query;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

public class TagQuery extends Query {
    private final String tagName;
    public TagQuery(String name) {
        tagName = name.toLowerCase();
    }

    @Override
    public boolean matches(ItemStack stack) {
        boolean success = !negated;
        List<TagKey<Item>> tags = stack.getTags().toList();
        boolean foundTag = false;
        for (var key: tags) {
            String name = Component.translatable(key.getTranslationKey()).getString();
            if (name.toLowerCase().contains(tagName)) {
                foundTag = true;
                break;
            }
        }
        return foundTag == success;
    }
}
