/** [Copyright]
 *  @author oujinliang
 *  Sep 13, 2012 5:19:13 PM
 */
package jinou.util.erlang

import scala.math.ScalaNumber

//====== Erlang Classes

abstract class ETerm(val termType: String)

class EPid(val node: Int, val id: Int, val serial: Int) extends ETerm("pid") {
    override def toString = "<%d.%d.%d>" format (node, id, serial)
}

class ETuple(val elements: List[ETerm]) extends ETerm("tuple") {
    override def toString = elements.mkString("{", ",", "}")
}

class EBin(val data: Array[Byte]) extends ETerm("binary") {
    override def toString = data.mkString("<<", ",", ">>")
}

class EList(val elements: List[ETerm]) extends ETerm("list") {
    override def toString = elements.mkString("[", ",",  "]")
}

class ENumber[T <: ScalaNumber](val number: T) extends ETerm("number") {
    override def toString = number.toString
}

class EAtom(val name: String, val quote: Boolean) extends ETerm("atom") {
    override def toString = if (quote) "\'" + name + "\'" else name
}

class EString(val value: String) extends ETerm("string") {
    override def toString = "\"" + value + "\""
}
