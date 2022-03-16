import java.text.DateFormat._
import java.util.{Date, Locale}
import scala.sys.error


// 只有一个实例的类：单实例 Singleton
object HelloWorld {

  // main 方法没有修饰 static，因为 Scala 没有静态成员，而是声明在单实例中
  def main(args: Array[String]) {
    println("Hello, world!")

    val now = new Date
    val df = getDateInstance(LONG, Locale.FRANCE)
    println(df format now)

    val c = new Complex(1.2, 3.4)
    println("imaginary part: " + c.im)

    // 模式匹配：表达式 (x+x)+(7+y)
    val exp: Tree = Sum(Sum(Var("x"), Var("x")), Sum(Const(7), Var("y")))
    val env: Environment = {
      case "x" => 5
      case "y" => 7
    }
    println("Expression: " + exp)
    println("Evaluation with x=5, y=7: " + eval(exp, env))
    println("Derivative relative to x:\n " + derive(exp, "x"))
    println("Derivative relative to y:\n " + derive(exp, "y"))


    // 泛型
    val cell = new Reference[Int]
    cell.set(13)
    println("Reference contains the half of " + (cell.get * 2))
  }

  type Environment = String => Int

  def eval(t: Tree, env: Environment): Int = t match {
    case Sum(l, r) => eval(l, env) + eval(r, env)
    case Var(n) => env(n)
    case Const(v) => v
  }

  def derive(t: Tree, v: String): Tree = t match {
    case Sum(l, r) => Sum(derive(l, v), derive(r, v))
    case Var(n) if (v == n) => Const(1)
    case _ => Const(0)
  }

}

class HelloWorld {

}

/**
 * 函数作为方法参数
 */
object Timer {
  def oncePerSecond(callback: () => Unit) {
    while (true) {
      callback();
      Thread sleep 1000
    }
  }

  def timeFlies() {
    println("time flies like an arrow...")
  }

  def main(args: Array[String]) {
    //    oncePerSecond(timeFlies)
    oncePerSecond(() => println("time flies like an arrow..."))
  }
}

/**
 * 类：可接受两个参数，相当于 java 中的构造方法
 */
class Complex(real: Double, imaginary: Double) {
  // get 方法
  //  def re() = real
  //  def im() = imaginary

  def re = real

  def im = imaginary

  // 重写父类方法
  override def toString() = "" + re + (if (im < 0) "" else "+") + im + "i"
}

/**
 * 模式匹配
 */
abstract class Tree

case class Sum(l: Tree, r: Tree) extends Tree

case class Var(n: String) extends Tree

case class Const(v: Int) extends Tree


/**
 * 特质（Traits）对应 java 中接口
 */
trait Ord {
  def <(that: Any): Boolean

  def <=(that: Any): Boolean = (this < that) || (this == that)

  def >(that: Any): Boolean = !(this <= that)

  def >=(that: Any): Boolean = !(this < that)
}

class DateDef(y: Int, m: Int, d: Int) extends Ord {
  def year = y

  def month = m

  def day = d

  override def toString(): String = year + "-" + month + "-" + day

  def <(that: Any): Boolean = {
    if (!that.isInstanceOf[DateDef])
      error("cannot compare " + that + " and a Date")

    val o = that.asInstanceOf[DateDef]
    (year < o.year) ||
      (year == o.year && (month < o.month || (month == o.month && day < o.day)))
  }
}


/**
 * 泛型
 */
class Reference[T] {
  private var contents: T = _
  def set(value: T) { contents = value }
  def get: T = contents
}

