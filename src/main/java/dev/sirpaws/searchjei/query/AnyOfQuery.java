package dev.sirpaws.searchjei.query;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AnyOfQuery extends Query {
    ArrayList<Query> queries;
    public AnyOfQuery(ArrayList<Query> queries) {
        this.queries = queries;
    }
    public AnyOfQuery(List<Query> queries) {
        this.queries = new ArrayList<>();
        for (var query: queries) {
            append(query);
        }
    }

    public void append(Query query) {
        queries.add(query);
    }

    @Override
    public boolean matches(ItemStack stack) {
        boolean success = !negated;
        for (Query q : queries) {
            if (q.matches(stack) == success) return true;
        }
        return false;
    }
}
