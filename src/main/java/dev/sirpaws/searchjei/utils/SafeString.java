package dev.sirpaws.searchjei.utils;

public class SafeString {
    private final String text;
    public SafeString(String text) { this.text = text; }

    public boolean indexInBounds(int index) {
        try {
            char unused = text.charAt(index);
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public Option<Character> charAt(int index) {
        try {
            return Option.of(text.charAt(index));
        } catch (IndexOutOfBoundsException e) {
            return Option.empty();
        }
    }

    public Option<String> substring(int beginIndex) {
        try {
            return Option.of(text.substring(beginIndex));
        } catch (IndexOutOfBoundsException e) {
            return Option.empty();
        }
    }

    public Option<String> substring(int beginIndex, int endIndex) {
        text.startsWith("e", 1);
        try {
            return Option.of(text.substring(beginIndex, endIndex));
        } catch (IndexOutOfBoundsException e) {
            return Option.empty();
        }
    }
}
