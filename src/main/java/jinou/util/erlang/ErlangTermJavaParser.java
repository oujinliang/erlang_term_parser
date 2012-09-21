/**
 * [Copyright] 
 * @author oujinliang
 * Sep 21, 2012 3:51:53 PM
 */
package jinou.util.erlang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jinou.util.erlang.ErlangTerms.BadFormatException;

/**
 *
 */
public class ErlangTermJavaParser {

    public static ErlangTerms.ETerm parse(String input) throws BadFormatException {
        try {
            return new ErlangTermJavaParser(input.toCharArray()).getTerm();
        } catch (Exception e) {
            throw new ErlangTerms.BadFormatException("Bad term format.", e);
        }
    }

    // ==========================================================
    // Instance memebers;
    private int pos = 0;
    private final int len;
    private final char[] chars;
    private ErlangTermJavaParser(char[] chars) {
        this.chars = chars;
        this.len = chars.length;
    }
    
    // 
    ErlangTerms.ETerm getTerm() {
        skipWhitespaces();
        ErlangTerms.ETerm term = getTermPure();
        skipWhitespaces();
        
        return term;
    }
    
    ErlangTerms.ETerm getTermPure() {
        char c = chars[pos];
        switch (c) {
            case '[': return getList();
            case '{': return getTuple();
            case '<': 
                return chars[pos + 1] == '<' ? getBinary() : getPid();
            case '\'': return getQuoteAtom();
            case '"' : return getString();
            default:
                if (isNumberStart(c)) {
                    return getNumber();
                } else if (isAtomStart(c)) {
                    return getAtom();
                } else {
                    throw new IllegalArgumentException("Invalid input");
                }
        }
    }
    
    private ErlangTerms.EAtom getQuoteAtom() {
        return new ErlangTerms.EAtom(getString('\''), true);
    }
    private ErlangTerms.EAtom getAtom() {
        int start = pos;
        while (pos < len && isInAtom(chars[pos])) {
            moveNext();
        }
        return new ErlangTerms.EAtom(new String(chars, start, pos - start), false);
    }
    
    private ErlangTerms.EList getList() {
        return new ErlangTerms.EList(getTermList('[', ']'));
    }
    
    private ErlangTerms.ETuple getTuple() {
        return new ErlangTerms.ETuple(getTermList('{', '}'));
    }
    
    private ErlangTerms.EString getString() {
        return new ErlangTerms.EString(getString('\"'));
    }
    
    private ErlangTerms.ENumber<? extends Number> getNumber() {
        if(chars[pos] == '+') {
            moveNext();
        }
        int start = pos;
        boolean isFloat = false;
        while (pos < len && isInNumber(chars[pos])) {
            char c = chars[pos];
            if (c == 'e' || c == 'E' || c == '.') {
                isFloat = true;
            }
            moveNext();
        }
        String numberStr = new String(chars, start, pos - start);
        
        return isFloat ? new ErlangTerms.ENumber<BigDecimal>(new BigDecimal(numberStr)) 
                       : new ErlangTerms.ENumber<BigInteger>(new BigInteger(numberStr));
    }
    
    private ErlangTerms.EBin getBinary() {
        eat("<<");
        skipWhitespaces();
        String value = getString('\"');
        skipWhitespaces();
        eat(">>");
        return new ErlangTerms.EBin(value.getBytes());
    }
    
    private ErlangTerms.EPid getPid() {
        eat('<');
        int node = getInt();
        eat('.');
        int id = getInt();
        eat('.');
        int  serial = getInt();
        eat('>');
        return new ErlangTerms.EPid(node, id, serial);
    }
    
    private List<ErlangTerms.ETerm> getTermList(char start, char end) {
        eat(start);
        List<ErlangTerms.ETerm> list = new ArrayList<ErlangTerms.ETerm>();
        while(pos < len) {
            skipWhitespaces();
            char c = chars[pos];
            if (c == end) {
                break;
            } else if (c == ',') {
                moveNext();
            } else {
                list.add(getTerm());
            }
        }
        eat(end);
        return list;
    }
    
    private int getInt() {
        skipWhitespaces();
        int start = pos;
        while (pos < len && isDigit(chars[pos])) {
            moveNext();
        }
        return Integer.parseInt(new String(chars, start, pos - start));
    }
    
    private String getString(char quote) {
        eat(quote);
        int start = pos;
        while (pos < len) {
            char c = chars[pos];
            if (c == '\\') {
                moveNext();
                if (isOct(chars[pos])) {
                    moveNext(3);
                } else {
                    moveNext();
                }
            } else if (c == quote) {
                break;
            } else {
                moveNext();
            }
        }
        eat(quote);
        return new String(chars, start, pos - start - 1);
    }
    
    private void skipWhitespaces() {
        while (pos < len && Character.isWhitespace(chars[pos])) {
            moveNext();
        }
    }
    
    private void eat(String s) {
        for(int i = 0; i < s.length(); ++i) {
            eat(s.charAt(i));
        }
    }
    
    private void eat(char c) {
        requires(chars[pos] == c, "Not expected char");
        moveNext();
    }
    
    private void moveNext() {  
        pos += 1;
    }
    
    private void moveNext(int i) { 
        pos += i;  
    }
    
    private void requires(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
    private static boolean isInAtom(char c) {
        return isAtomStart(c) || (c >= 'A' && c <= 'Z') || c == '_' || c == '@';
    }
    
    private static boolean isAtomStart(char c) {
        return c >= 'a' && c <= 'z';
    }
    
    private static boolean isInNumber(char c) {
        return isNumberStart(c) || c == 'e' || c == 'E' || c == '.' || c == '#';
    }
    
    private static boolean isNumberStart(char c) {
        return isDigit(c) || c == '+' || c == '-';
    }
    
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private static boolean isOct(char c) {
        return c >= '0' && c <= '7';
    }
}
