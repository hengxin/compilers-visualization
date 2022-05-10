package org.antlr.v4.runtime;

public class StringTools {
    public static String replace(String s) {
        if (s == null) return null;
        if (s.trim().isBlank()) {
            return "'" + s + '\'';
        }
        s = s.replace("\n","\\n");
        s = s.replace("\r","\\r");
        s = s.replace("\t","\\t");
        return s;
    }
    public static String replace(int i) {
        return switch (i) {
            case '\n' -> "\\n";
            case '\t' -> "\\t";
            case '\r' -> "\\r";
            default -> String.valueOf((char) i);
        };
    }
}
