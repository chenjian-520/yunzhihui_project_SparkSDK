package com.tm.dl.javasdk.dpspark.scala

import scala.collection.mutable

/**
  * Description:  com.tm.dl.javasdk.dpspark.scala
  * Copyright: © 2017 FanLei. All rights reserved.
  * Company: NULL
  *
  * @author FL
  * @version 1.0
  * @timestamp 2019/6/25
  */
class RunTestScala extends DPSparkScalaBase {
  override def main(argsMap: mutable.HashMap[String, Object]): Unit = {
    // to something with scala
    println("=========="+argsMap.get("dpappName")+"=============")
    println("=========="+argsMap.get("dprunclass")+"============")
  }
}
