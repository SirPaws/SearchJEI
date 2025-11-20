package dev.sirpaws.searchjei.utils;

import org.jetbrains.annotations.NotNull;

public class MixinUtils {

    @SuppressWarnings("unchecked")
    public static<T> T asTarget(@NotNull Object mixin) { return (T)mixin; }
}
