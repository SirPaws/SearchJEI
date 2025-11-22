package dev.sirpaws.searchjei.query;

import dev.sirpaws.searchjei.utils.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum QueryType {
    DEFAULT("", NameQuery::new),
    MOD("@", ModQuery::new),
    TOOLTIP("$", TooltipQuery::new),
    TAG("#", TagQuery::new)
    ;

    public final String prefix;
    public final Function<String, Query> queryConstructor;
    private QueryType(String prefix, Function<String, Query> queryConstructor) {
        this.prefix = prefix;
        this.queryConstructor = queryConstructor;
    }

    public static Option<Query> fromText(String text) {
        for (var value: Arrays.stream(QueryType.values()).toList().reversed()) {
            if (text.startsWith(value.prefix)) {
                String withoutPrefix = text.substring(value.prefix.length());
                if (withoutPrefix.chars().anyMatch(Character::isWhitespace)) {
                    List<Query> queries = Arrays.stream(withoutPrefix.split("\\s")).map(value.queryConstructor)
                            .toList();
                    return Option.of(new AnyOfQuery(queries));
                }
                return Option.of(value.queryConstructor.apply(text.substring(value.prefix.length())));
            }
        }
        return Option.empty();
    }
}
