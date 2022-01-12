package flink.ml

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.ml.classification.SVM
import org.apache.flink.ml.{MLUtils, math}
import org.apache.flink.ml.common.LabeledVector
import org.apache.flink.ml.math.DenseVector

import scala.sys.process.processInternal.IOException


class Test {

  val env = ExecutionEnvironment.getExecutionEnvironment
  val survival = env.readCsvFile[(String, String, String, String)]("/path/to/haberman.data")

  val survivalLV = survival
    .map { tuple =>
      val list = tuple.productIterator.toList
      val numList = list.map(_.asInstanceOf[String].toDouble)
      LabeledVector(numList(3), DenseVector(numList.take(3).toArray))
    }

  val astroTrain: DataSet[LabeledVector] = MLUtils.readLibSVM(env, "/path/to/svmguide1")
  val astroTest: DataSet[(math.Vector, Double)] = MLUtils.readLibSVM(env, "/path/to/svmguide1.t")
    .map(x => (x.vector, x.label))

  val svm = SVM()
    .setBlocks(env.getParallelism)
    .setIterations(100)
    .setRegularization(0.001)
    .setStepsize(0.1)
    .setSeed(42)

  svm.fit(astroTrain)

  val evaluationPairs: DataSet[(Double, Double)] = svm.evaluate(astroTest)

  def display(game: Option[String]) = game match {
    case Some(s) => s
    case None => "unknown"
  }

  def describe(x: Any): String = x match {
    case 1 => "one"
    case false => "False"
    case "hi" => "hello, world!"
    case Nil => "the empty list"
    case e: IOException => "this is an IOException"
    case s: String if s.length > 10 => "a long string"
    case _ => "something else"
  }
}
