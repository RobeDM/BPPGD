import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd._

import org.apache.spark.mllib.util.MLUtils

import java.io._
import java.lang.System
import scala.util.Random


object TestKernelSVM {
  def main(args: Array[String]) {

    if (args.length <= 6 ) {
      println("Usage: /path/to/spark/bin/spark-submit --packages amplab:spark-indexedrdd:0.4.0" +
        "target/scala-2.11/ppackubuntu_2.11-1.0.jar <data file>")
      sys.exit(1)
    }

    val logFile = "README.md" // Should be some file on your system
    val conf = new SparkConf().setAppName("Ppack KernelSVM Test")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val action = args(0)
    val data =  MLUtils.loadLibSVMFile(sc, args(1),args(6).toInt)
    println("Features: "+data.first.features.size)
    val lambda = args(2).toDouble
    val gamma = 0.5/math.pow(args(3).toDouble,2.0)
    val num_iter = (args(4)).toLong

    println("Lambda: "+lambda.toString)
    println("Sigma: "+args(3)+", Gamma: "+gamma.toString)
    println("Num iters: "+num_iter.toString)

    val m = data.count()

    var pack_size = 100

    val pw = new PrintWriter(new File(args(5) ))

    if (action=="VALIDATION"){
        println(action+" on dataset "+args(1))
        val rand = new scala.util.Random
        val RDDwithFold = data.map(x => (rand.nextInt( (4) + 1 ),x))
        println("Total dataset size: "+data.count())
        var a =0
        var meanAcc = 0.0
        var meanAUC = 0.0
        for( a <- 0 to 4){
          println("Creating fold "+a)
          val trainRDD = RDDwithFold.filter(x => (x._1 != a)).map(x => x._2)
          val testRDD  = RDDwithFold.filter(x => (x._1 == a)).map(x => x._2)
          println("Training set size: "+trainRDD.count())
          println("Test set size: "+testRDD.count())
          val t1 = System.currentTimeMillis
          var svm = new KernelSVM(trainRDD, lambda, "rbf", gamma)
          svm.train(num_iter,pack_size)
          val t2 = System.currentTimeMillis
          val runtime = (t2 - t1)/1000
          val results = svm.getAccuracyAndAUC(testRDD.collect(),sc)
          var ss = "Training time: " + runtime.toString + " Accuracy: " + results._1.toString+ " AUC: " + results._2.toString + "\n"
          meanAcc = meanAcc + results._1
          meanAUC = meanAUC + results._2
          pw.write(ss)
          print(ss)

        }

      var ss = "Mean_Accuracy: " + meanAcc/5.0 + " Mean_AUC: " + meanAUC/5.0 + "\n"
      pw.write(ss)
      print(ss)

    }else if(action=="TEST"){
         println(action+" trained with " + args(1) +" on dataset "+args(7))
         val t1 = System.currentTimeMillis
         var svm = new KernelSVM(data, lambda, "rbf", gamma)
         svm.train(num_iter,pack_size)
         val t2 = System.currentTimeMillis
         val runtime = (t2 - t1)/1000

         val testdata =  MLUtils.loadLibSVMFile(sc, args(7),args(6).toInt)

         println("Training time: "+runtime.toString)
         var s1 = "Training time: "+runtime.toString+"\n"
         pw.write(s1)

         val results = svm.getAccuracyAndAUC(testdata.collect(),sc)
         var s0 = "Testing time: "+results._3.toString+"\n"
         pw.write(s0)
         println("Accuracy: "+results._1.toString)
         println("AUC: "+results._2.toString)

         var s2 = "Accuracy: "+results._1.toString+" AUC: "+results._2.toString+"\n"
         pw.write(s2)


    }else{
      print("Action not known")
    }

    pw.close

/*
    val pw = new PrintWriter(new File("result.txt" ))

    for (num_iter <- iterations) {

      val t1 = System.currentTimeMillis
      val svm = new KernelSVM(training, lambda, "rbf", gamma)
      //val svm = new KernelSVM(training, 1.0/m, "rbf", 0.0001)
      svm.train(num_iter,pack_size)
      val t2 = System.currentTimeMillis
      val runtime = (t2 - t1)/1000

      var ss = m.toString + " " + num_iter.toString + " " + pack_size.toString + " " + svm.getAccuracy(test).toString + " " + runtime.toString + "\n"
      pw.write(ss)
      print(ss)
      sys.exit()
    }

    pw.close
    */
  }
}
