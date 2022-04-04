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
}
