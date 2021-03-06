/** [Copyright]
 *  @author oujinliang
 *  Sep 13, 2012 2:15:05 PM
 */
package jinou.util.erlang

import org.junit._
import org.junit.Assert._
import scala.math.ScalaNumber
/**
 */
class ErlangTermScalaParserTest {

    @Test def testQuoteAtom() {
       testAtom("Abort_ss#@", """ 'Abort_ss#@' """)
    }
    
    @Test def testAtom() {
        testAtom("aBxc@_deff", """ aBxc@_deff  """)
    }
    
    private def testAtom(expected: String, input: String) {
        val atom = parse[EAtom](input)
        assertEquals(expected, atom.name)
    }
    
    @Test def testNumber() {
        testNumber(BigInt("1234567890"), " +1234567890   ")
        testNumber(BigInt("-1234567890"), " -1234567890   ")
        
        testNumber(BigDecimal("123.4567890"), " +123.4567890   ")
        testNumber(BigDecimal("-123.4567890"), " -123.4567890   ")
        
        testNumber(BigDecimal("-1.25E6"), " -1.25E+6   ")
    }
    
    private def testNumber[T <: ScalaNumber](expected: T, input: String) {
        val number = parse[ENumber[T]](input)
        println(number)
        assertEquals(expected.toString(), number.toString())
    }
    
    @Test def testList() {
        val list = parse[EList](""" [[ abc, 'A_ss', 123, -2.34, {good, "this is a string\" Yes \098", 
                
        name, 
                
                %% This is a comment
        
        bad}, <<"hello">>, <0.9.1234> ], {this, that}, [] , {}]""")
        println (list)
    }
    private def parse[T <: ETerm](input: String) = ErlangTermScalaParser.parse(input).asInstanceOf[T]
}