/** [Copyright]
 *  @author oujinliang
 *  Sep 13, 2012 2:15:05 PM
 */
package jinou.util.erlang

import org.junit._
import org.junit.Assert._
/**
 */
class ErlangTermScalaParserTest {

    @Test def testQuoteAtom() {
       val atom = ErlangTermScalaParser.parse(""" 'Abort_ss#@' """)
       println(atom)
    }
    
    
}