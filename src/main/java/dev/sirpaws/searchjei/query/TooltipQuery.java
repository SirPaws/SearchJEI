package dev.sirpaws.searchjei.query;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class TooltipQuery extends Query {
    private final String name;
    public TooltipQuery(String name) {
        this.name = name;
    }
    @Override
    public boolean matches(ItemStack stack) {
        Minecraft client = Minecraft.getInstance();
        return stack.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL)
                .stream().anyMatch(
                        line -> debug$contains(line.getString().toLowerCase(), name)
                ) == !negated;
    }
    boolean debug$contains(String a, String b) {
        return a.contains(b);
    }
}
