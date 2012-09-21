/** [Copyright]
 *  @author oujinliang
 *  Sep 13, 2012 2:15:05 PM
 */
package jinou.util.erlang

import org.junit._
import org.junit.Assert._
import ErlangTerms._

/**
 */
class ErlangTermJavaParserTest {

    @Test def testQuoteAtom() {
       testAtom("Abort_ss#@", """ 'Abort_ss#@' """)
    }
    
    @Test def testAtom() {
        testAtom("aBxc@_deff", """ aBxc@_deff  """)
    }
    
    private def testAtom(expected: String, input: String) {
        val atom = parse[ErlangTerms.EAtom](input)
        assertEquals(expected, atom.name)
    }
    
    @Test def testNumber() {
        testNumber(BigInt("1234567890"), " +1234567890   ")
        testNumber(BigInt("-1234567890"), " -1234567890   ")
        
        testNumber(BigDecimal("123.4567890"), " +123.4567890   ")
        testNumber(BigDecimal("-123.4567890"), " -123.4567890   ")
        
        testNumber(BigDecimal("-1.25E6"), " -1.25E+6   ")
    }
    
    private def testNumber[T <: Number](expected: T, input: String) {
        val number = parse[ErlangTerms.ENumber[T]](input)
        println(number)
        assertEquals(expected.toString(), number.toString())
    }
    
    @Test def testList() {
        val list = parse[ErlangTerms.EList](""" [[ abc, 'A_ss', 123, -2.34, {good, "this is a string\" Yes \098", 
                
        name, 
        
        bad}, <<"hello">>, <0.9.1234> ], {this, that}, [] , {}]""")
        println (list)
    }
    private def parse[T <: ErlangTerms.ETerm](input: String) = ErlangTermJavaParser.parse(input).asInstanceOf[T]
}