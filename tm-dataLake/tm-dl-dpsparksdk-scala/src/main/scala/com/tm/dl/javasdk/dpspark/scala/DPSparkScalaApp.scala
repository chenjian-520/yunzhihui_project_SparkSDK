package com.tm.dl.javasdk.dpspark.scala

import com.google.gson.{JsonObject, JsonParser}
import com.tm.dl.javasdk.dpspark.DPSparkApp

import scala.collection.mutable


/**
  * Description:  com.tm.dl.javasdk.dpspark.scala
  * Copyright: © 2017 FanLei. All rights reserved.
  * Company: NULL
  *
  * @author FL
  * @version 1.0
  * @timestamp 2019/6/21
  */
object DPSparkScalaApp {
  private val gson = new JsonParser()

  def main(args: Array[String]): Unit = {
    val argString = args(0)
    var argsElement = gson.parse(argString).asInstanceOf[JsonObject]
    argsElement.addProperty("callSource","scala")

    val runClass = argsElement.get("dprunclass").toString.replaceAll("\"","")
    val dpuserid = argsElement.get("dpuserid")
    val dpappName = argsElement.get("dpappName")
    val dpmaster = argsElement.get("dpmaster")

    val argMap = new mutable.HashMap[String, Object]()
    argMap.put("dpuserid", dpuserid)
    argMap.put("dpappName", dpappName)
    argMap.put("dpmaster", dpmaster)
    argMap.put("dprunclass", runClass)
    argMap.put("callSource","scala")
    //执行入口类
    DPSparkApp.main(Array(argsElement.toString))
    //执行任务类
    val runDps: DPSparkScalaBase = Class.forName(runClass.toString).newInstance().asInstanceOf[DPSparkScalaBase]
    runDps.main(argMap)
  }
}
