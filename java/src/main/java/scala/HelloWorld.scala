package scala

import java.text.DateFormat._
import java.util.{Date, Locale}


// 只有一个实例的类：单实例 Singleton
object HelloWorld {

  // main 方法没有修饰 static，因为 Scala 没有静态成员，而是声明在单实例中
  def main(args: Array[String]) {
    println("Hello, world!")

    val now = new Date
    val df = getDateInstance(LONG, Locale.FRANCE)
    println(df format now)
  }

}

class HelloWorld {

}
