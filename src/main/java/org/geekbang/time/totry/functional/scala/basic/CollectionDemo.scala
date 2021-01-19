package org.geekbang.time.totry.functional.scala.basic

/**
 * 集合
 */
object CollectionDemo {
  def main(args: Array[String]): Unit = {
    // 定义整型 List
    val list = List(1,2,3,4)
    println(list)
    // 定义 Set
    val set = Set(1,3,5,7)
    println(set)
    // 定义 Map
    val map = Map("one" -> 1, "two" -> 2, "three" -> 3)
    println(map)
    // 创建两个不同类型元素的元组
    val tuples = (10, "Runoob")
    println(tuples)
    // 定义 Option
    val option:Option[Int] = Some(5)
    println(option)

  }
}
