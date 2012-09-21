/**
 * [Copyright] 
 * @author oujinliang
 * Sep 21, 2012 12:17:49 PM
 */
package jinou.util.erlang;

import java.util.List;

/**
 *
 */
public final class ErlangTerms {

    public static abstract class ETerm {
        public final String termType;
        ETerm(String type) {
            this.termType = type;
        }
    }
    
    public static class EPid extends ETerm {
        public final int node;
        public final int id;
        public final int serial;
        EPid(int node, int id, int serial) {
            super("pid");
            this.node = node;
            this.id = id;
            this.serial = serial;
        }
        
        @Override public String toString() {
            return String.format("<%d.%d.%d>", node, id, serial);
        }
    }
    
    public static class ETuple extends ETerm {
        public final List<ETerm> terms;
        ETuple(List<ETerm> terms) {
            super("tuple");
            this.terms = terms;
        }
        @Override public String toString() {
            return mkString(terms, "{", "}", ",");
        }
    }
    
    public static class EBin extends ETerm {
        public final byte[] data;
        EBin(byte[] data) {
            super("bin");
            this.data = data;
        }
        
        @Override public String toString() {
            return  "<<\"" + new String(data) + "\">>";
        }
    }
    
    public static class EList extends ETerm {
        public final List<ETerm> terms;
        EList(List<ETerm> terms) {
            super("list");
            this.terms = terms;
        }
        
        @Override public String toString() {
            return mkString(terms, "[", "]", ",");
        }
    }
    
    public static class ENumber<T extends Number> extends ETerm {
        public final T number;
        ENumber(T number) {
            super("number");
            this.number = number;
        }
        
        @Override public String toString() {
            return number.toString();
        }
    }
    
    public static class EAtom extends ETerm {
        public final String name;
        public final boolean quote;
        EAtom(String name, boolean quote) {
            super("atom");
            this.name = name;
            this.quote = quote;
        }
        
        @Override public String toString() {
            return quote ? "\'" + name + "\'" : name;
        }
    }
    
    public static class EString extends ETerm {
        public final String value;
        EString(String value) {
            super("string");
            this.value = value;
        }
        
        @Override public String toString() {
            return "\"" + value + "\"";
        }
    }
    
    
    public static class BadFormatException extends Exception {
        private static final long serialVersionUID = -3858917885226887795L;
        public BadFormatException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    // =============================================================
    // HELPER methods.
    static <T> String mkString(Iterable<T> list, String start, String end, String sep) {
        StringBuilder sb = new StringBuilder(start);
        boolean isFirst = true;
        for (T t : list) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(sep);
            }
            sb.append(t.toString());
        }
        return sb.append(end).toString();
    }
}
