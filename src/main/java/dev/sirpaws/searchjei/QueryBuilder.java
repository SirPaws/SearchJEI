package dev.sirpaws.searchjei;

import com.mojang.datafixers.util.Pair;
import dev.sirpaws.searchjei.query.*;
import dev.sirpaws.searchjei.utils.Option;
import dev.sirpaws.searchjei.utils.SafeString;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;

public class QueryBuilder {
    static Query fromText(String text) {
        return new Parser(text).parse();
    }

    private static class Parser {
        private static final int AND    = '&'; // '&' or '&&'
        private static final int OR     = '|';  // '|' or '||'
        private static final int RPAREN = '('; // '('
        private static final int LPAREN = ')';// ')'
        private static final int MINUS  = '-';// ')'
        private static final int TEXT   = -1;
        private static final int EOF    = -2;
        private static final int STRING = -3;
        private final ArrayList<Integer> previousState;
        private Option<String> lookahead;
        private final SafeString text;
        private int pos;

        Parser(String text) {
            this.text = new SafeString(text);
            pos = 0;
            previousState = new ArrayList<>();
            lookahead = Option.empty();
        }

        Query parse() {
            Option<Query> maybeQuery = parseExpression();
            while (maybeQuery.isEmpty() && !tokenIs(EOF)) {
                maybeQuery = parseExpression();
            }
            if (maybeQuery.isEmpty()) return new NullQuery();
            Query query = maybeQuery.get();
            ArrayList<Query> queries = new ArrayList<>();
            queries.add(query);

            while (!tokenIs(EOF)) {
                maybeQuery = parseExpression();
                maybeQuery.ifPresent(queries::add);
            }

            if (queries.size() == 1)
                return query;
            else return new AnyOfQuery(queries);
        }

        Option<Query> parseExpression() {
            Option<Query> maybeElem = parseNegated();
            if (maybeElem.isEmpty()) return maybeElem; // equivalent to return Option.empty()
            Query elem = maybeElem.get();

            while (tokenIs(new int[]{ AND, OR, TEXT })) {
                if (tokenIs(TEXT)) {
                    elem = makeQueryIntoAnyOf(elem, parseElement().get());
                } else {
                    Pair<Integer, String> op = consume();
                    pushState();
                    maybeElem = parseElement();
                    if (maybeElem.isPresent()) {
                        popSuccess();
                        elem = switch (op.getFirst()) {
                            case AND -> new AndQuery(elem, maybeElem.get());
                            case OR  -> new OrQuery(elem, maybeElem.get());
                            default  -> makeQueryIntoAnyOf(makeQueryIntoAnyOf(elem, new NameQuery(op.getSecond())),
                                    maybeElem.get());
                        };
                    } else {
                        elem = makeQueryIntoAnyOf(elem, new NameQuery(op.getSecond()));
                        popFailure();
                    }
                }
            }

            return Option.of(elem);
        }

        Option<Query> parseNegated() {
            if (tokenIs('-')) {
                Pair<Integer, String> op = consume();

                pushState();
                Option<Query> maybeElem = parseElement();
                if (maybeElem.isPresent()) {
                    maybeElem.get().negated = true;
                    popSuccess();
                    return maybeElem;
                } else {
                    popFailure();
                    return Option.of(new NameQuery(op.getSecond()));
                }
            }
            return parseElement();
        }

        Option<Query> parseElement() {
            if (tokenIs('(')) {
                Pair<Integer, String> lparen = consume();

                pushState();
                Option<Query> maybeElem = parseExpression();
                if (tokenIs(')')) {
                    popSuccess();
                    consume();
                    return maybeElem;
                } else {
                    popFailure();
                    return Option.of(new NameQuery(lparen.getSecond()));
                }
            }
            else if (tokenIs(TEXT) || tokenIs(STRING)) {
                Pair<Integer, String> token = consume();
                return QueryType.fromText(token.getSecond());
            }
            else {
                consume();
                return Option.empty();
            }
        }

        void pushState() {
            previousState.add(pos);
        }
        void popSuccess() {
            previousState.removeLast();
        }
        void popFailure() {
            pos = previousState.removeLast();
        }

        Query makeQueryIntoAnyOf(Query left, Query right) {
            if (left instanceof AnyOfQuery) {
                ((AnyOfQuery)left).append(right);
                return left;
            } else {
                ArrayList<Query> queries = new ArrayList<>();
                queries.add(left);
                queries.add(right);
                return new AnyOfQuery(queries);
            }
        }

        boolean tokenIs(int[] kinds) {
            for (int kind: kinds) {
                if (tokenIs(kind)) return true;
            }
            return false;
        }

        boolean tokenIs(int kind) {
            return lookAheadToTokenKind() == kind;
        }

        Pair<Integer, String> consume() {
            Option<String> current = lookAhead();
            if (current.isEmpty()) {
                return new Pair<>(EOF, "");
            } else {
                Pair<Integer, String> token = switch (current.get()) {
                    case "&", "&&" -> new Pair<>(AND,    current.get()); // '&' or '&&'
                    case "|", "||" -> new Pair<>(OR,     current.get());   // '|' or '||'
                    case "("       -> new Pair<>(LPAREN, current.get());  // '('
                    case ")"       -> new Pair<>(RPAREN, current.get()); // ')'
                    case "-"       -> new Pair<>(MINUS,  current.get()); // ')'
                    default        -> new Pair<>(isString(current.get()) ? STRING : TEXT, current.get());
                };
                lookahead = Option.empty();
                pos += current.get().length();
                if (token.getFirst() == STRING) {
                    token = new Pair<>(token.getFirst(), token.getSecond().replace("\"", ""));
                }
                return token;
            }
        }

        int lookAheadToTokenKind() {
            return lookAhead().map(str ->
                switch (str) {
                case "&", "&&" -> AND;
                case "|", "||" -> OR;
                case "("       -> LPAREN;
                case ")"       -> RPAREN;
                case "-"       -> MINUS;
                default        -> isString(str) ? STRING : TEXT;
            }).orElse(EOF);
        }

        boolean isString(String text) {
            if (text.startsWith("\"") && text.endsWith("\"") && text.length() >= 2)
                return true;
            return text.startsWith("\"", 1) && text.endsWith("\"") && text.length() >= 3;
        }

        Option<String> lookAhead() {
            if (lookahead.isPresent()) return lookahead;
            if (!text.indexInBounds(pos)) return Option.empty();

            Option<Character> maybeCharacter = text.charAt(pos);
            Option<String> tokenText = maybeCharacter.map(c -> switch (c) {
                case '(' -> "("; // '('
                case ')' -> ")";// ')'
                case '-' -> "-";// ')'
                default -> null;
            });
            if (tokenText.isPresent()) {
                lookahead = tokenText;
            } else if (maybeCharacter.isEmpty()) {
                lookahead = Option.empty();
            } else if (maybeCharacter.get() == '&') {
                if (text.charAt(pos + 1).isPresentAnd(c -> c == '&')) {
                    lookahead = Option.of("&&");
                } else {
                    lookahead = Option.of("&");
                }
            } else if (maybeCharacter.get() == '|') {
                if (text.charAt(pos + 1).isPresentAnd(c -> c == '|')) {
                    lookahead = Option.of("||");
                } else {
                    lookahead = Option.of("|");
                }
            } else if (!maybeParseString()) {
                if (maybeCharacter.get() == '"') {
                    lookahead = Option.of("\"");
                } else {
                    int index = pos;
                    StringBuilder builder = new StringBuilder();
                    outer:
                    while (text.indexInBounds(index)) {
                        char c = text.charAt(index).orElse('\0'); // should always be valid
                        switch (c) {
                            case '|':
                            case '&':
                            case '(':
                            case ')':
                            case '"':
                            case '-':
                                break outer;
                            default:
                                builder.append(c);
                        }
                        index++;
                    }

                    if (builder.isEmpty()) {
                        lookahead = Option.empty();
                    } else {
                        lookahead = Option.of(builder.toString());
                    }
                }
            }

            return lookahead;
        }

        boolean maybeParseString() {
            boolean isString = text.charAt(pos).isPresentAnd(c -> switch (c) {
                case '#', '$', '@' -> text.charAt(pos + 1).isPresentAnd(c1 -> c1 == '"');
                case '"' -> true;
                default -> false;
            });
            if (!isString) return false;

            int endPos = pos + (text.charAt(pos).get() == '"' ? 1 : 2);
            while (text.charAt(endPos).isPresent()) {
                char character = text.charAt(endPos).get();
                if (character == '"') {
                    break;
                }
                endPos++;
            }
            if (text.charAt(endPos).isPresentAnd(c -> c == '"')) {
                lookahead = text.substring(pos, endPos + 1);
            }
            return lookahead.isPresent();
        }
    }
}
