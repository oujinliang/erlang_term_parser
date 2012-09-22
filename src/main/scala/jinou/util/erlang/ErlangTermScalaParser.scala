/** [Copyright]
 *  @author oujinliang
 *  Sepbjec 13, 2012 1:45:52 PM
 */
package jinou.util.erlang

import scala.collection.mutable.ListBuffer

class BadTermFormatException(msg: String, cause: Throwable) extends Exception(msg, cause)

/**
 */
object ErlangTermScalaParser {
    val numberstarts = "+-0123456789" toSet 
    val numbers = "+-0123456789eE#." toSet
    val atoms = ('a' to 'z') ++ ('A' to 'Z') ++ "_@" toSet
    
    private [erlang] def isNumberStart(char: Char) = numberstarts.contains(char)
    private [erlang] def isAtomStart(char: Char) = char >= 'a' && char <= 'z'
    private [erlang] def isDigit(char: Char) = char >= '0' && char <= '9'
        
    def parse(input: String): ETerm = {
        try {
            new Parser(input.toCharArray()).getTerm
        } catch {
            case e: Exception => throw new BadTermFormatException("Bad term format", e)
        }
    } 
}

// The Parser
private class Parser(chars: Array[Char]) {
    import ErlangTermScalaParser._
    
    require(chars != null)
    private val len = chars.length
    private var pos: Int = 0
    
    def getTerm(): ETerm = {
        skipWhitespaces
        
        val char = chars(pos)
        val term = char match {
            case '[' => getList
            case '{' => getTuple
            case '<' => chars(pos + 1) match {
                            case '<' => getBinary
                            case _   => getPid
                        }
            case '\'' => getQuoteAtom
            case '"' => getString
            case _ =>
                if (isNumberStart(char)) 
                    getNumber
                else if(isAtomStart(char))
                    getAtom
                else
                   errorArgs("invalid input")     
        }
        skipWhitespaces
        term
    }
    
    private def getQuoteAtom(): EAtom = new EAtom(getString('\''), true)
    
    private def getAtom(): EAtom = {
        val start = pos
        while(pos < len && atoms.contains(chars(pos))) 
            moveNext
        new EAtom(new String(chars, start, pos - start), false)
    }
    
    private def getList(): EList = new EList(getTermList('[', ']'))
    
    private def getTuple(): ETuple = new ETuple(getTermList('{', '}'))
    
    private def getString(): EString = new EString(getString('\"'))
    
    private def getNumber(): ENumber[_] = {
        if (chars(pos) == '+')
            moveNext
        val start = pos
        var isFloat = false
        while (pos < len && numbers.contains(chars(pos))) {
            val ch = chars(pos)
            if (ch == 'e' || ch == 'E' || ch == '.') 
                isFloat = true
            moveNext
        }
        val numberStr = new String(chars, start, pos - start)
        
        if (isFloat) new ENumber(BigDecimal(numberStr)) else new ENumber(BigInt(numberStr))
    }
    
    private def getBinary(): EBin = {
        eat("<<")
        skipWhitespaces
        val str = getString('\"')
        skipWhitespaces
        eat(">>")
        new EBin(str.getBytes())
    }
    
    private def getPid(): EPid = {
        eat('<')
        val node = getInt
        eat('.')
        val id = getInt
        eat('.')
        val serial = getInt
        eat('>')
        new EPid(node, id, serial)
    }
    
    private def getTermList(start: Char, end: Char) = {
        eat(start)
        val list = new ListBuffer[ETerm]()
        var isEnd = false
        while(!isEnd) {
            skipWhitespaces
            chars(pos) match {
                case `end` => isEnd = true  
                case ',' => moveNext
                case _   => list += getTerm
            }
        }
        eat(end)
        list.toList
    }
    
    private def skipWhitespaces() {
        while(pos < len && Character.isWhitespace(chars(pos)) ) 
            moveNext
        if (pos < len && chars(pos) == '%') {
            while(pos < len && chars(pos) != '\n')
                moveNext
            eatOptional('\r')
        }
    }
    
    private def getInt() = {
        skipWhitespaces
        val start = pos
        while (pos < len && isDigit(chars(pos)))
            moveNext
        new String(chars, start, pos - start).toInt
    }
    
    private def eatOptional(c: Char) {
        if (pos < len && chars(pos) == c)
            moveNext
    }
    
    private def eat(str: String) {
        str foreach {c => eat(c) }
    }
    
    private def eat(c: Char) {
        require(chars(pos) == c)
        moveNext
    }
    
    private def getString(quote: Char) = {
        eat(quote)
        val start = pos
        var found = false
        while(pos < len && !found) {
            chars(pos) match {
                case '\\'   =>
                    moveNext
                    chars(pos) match { 
                        case '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7' => moveNext(3)
                        //case 'u' => moveNext(4)
                        case _ => moveNext
                    }
                case `quote` => found = true
                case _      => moveNext
            }
        }
        eat(quote)
        new String(chars, start, pos - start - 1)
    }
    private def moveNext() { pos += 1 }
    private def moveNext(i: Int) { pos += i }
    
    private def errorArgs(error: String): Nothing = throw new IllegalArgumentException(error)
} 
