/** [Copyright]
 *  @author oujinliang
 *  Sepbjec 13, 2012 1:45:52 PM
 */
package jinou.util.erlang

/**
 */
object ErlangTermScalaParser {
    val numbers = "+-0123456789" toSet 
    val atoms = ('a' to 'z') ++ ('A' to 'Z') ++ "_" toSet
    
    private [erlang] def isNumberStart(char: Char) = numbers.contains(char)
    private [erlang] def isAtomStart(char: Char) = atoms.contains(char)
    
    def parse(input: String): ETerm = new Parser(input.toCharArray()).getTerm
    
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
        char match {
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
    }
    
    private def getQuoteAtom(): EAtom = {
        eat('\'')
        val start = pos
        moveTo('\'', '\\')
        eat('\'')
        new EAtom(new String(chars, start, pos - start - 1))
    }
    
    private def getAtom(): EAtom = {
        val start = pos
        null
    }
    
    private def getList(): EList = {
        null
    }
    
    private def getTuple(): ETuple = {
        null
    }
    
    private def getString(): EString = {
        null
    }
    
    private def getNumber(): ENumber = {
        null
    }
    
    private def getBinary(): EBin = {
        null
    }
    
    private def getPid(): EPid = {
        null
    }
    
    private def skipWhitespaces() {
        while(pos < len && Character.isWhitespace(chars(pos)) ) 
            moveNext
    }
    
    private def eat(c: Char) {
        require(chars(pos) == c)
        moveNext
    }
    
    private def moveTo(ch: Char, escapeChar: Char) {
        var found = false
        while(pos < len && !found) {
            chars(pos) match {
                case `escapeChar` => moveNext(2)
                case `ch`         => found = true
                case _           => moveNext
            }
        }
    }
    private def moveNext() { pos += 1 }
    private def moveNext(i: Int) { pos += i }
    
    private def errorArgs(error: String): Nothing = throw new IllegalArgumentException(error)
} 
