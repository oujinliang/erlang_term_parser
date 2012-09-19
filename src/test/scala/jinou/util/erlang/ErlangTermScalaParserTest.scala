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

    @Test def test() {
        val a = (('a' to 'z') ++ ('A' to 'Z') ++ "_") toArray
        
        println(a mkString)
    }
    
}