/** [Copyright]
 *  @author oujinliang
 *  Sep 13, 2012 5:19:13 PM
 */
package jinou.util.erlang

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

class ENumber(val number: BigDecimal) extends ETerm("number") {
    override def toString = number.toString
}

class EAtom(val name: String) extends ETerm("atom") {
    override def toString = name
}

class EString(val value: String) extends ETerm("string") {
    override def toString = "\"" + value + "\""
}
