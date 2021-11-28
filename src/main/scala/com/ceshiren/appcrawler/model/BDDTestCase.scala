package com.ceshiren.appcrawler.model

import com.ceshiren.appcrawler.driver.{BaseDDT, MockDDT, SeleniumDDT}
import com.ceshiren.appcrawler.utils.Log.log

import scala.collection.mutable.ListBuffer

case class BDDTestCase(
                        name: String = "",
                        given: List[Map[String, Any]] = List(),
                        when: List[Map[String, Any]] = List(),
                        `then`: List[Map[String, Any]] = List()
                      ) {
  private val bddDriverList: ListBuffer[AnyRef] = ListBuffer(new BaseDDT);

  def run(): Unit = {
    when.foreach(step => {
      runStep(step)
    })
  }

  def runStep(step: Map[String, Any]): Any = {
    log.debug(step)
    val methodName = getMethodName(step)
    if (methodName == "driver") {
      val arg = getArgs(step).toString
      create(arg)
    }

    bddDriverList.foreach(driver => {

      driver.getClass.getDeclaredMethods.filter(_.getName == methodName).foreach(method => {
        log.debug(method)
        if (method.getParameterCount == 0) {
          return method.invoke(driver)
        } else{
          val args = getArgs(step).asInstanceOf[List[Any]]

          method.getParameterTypes.foreach(paramType=>{
            
          })

//          val firstParamType = method.getParameterTypes.toList.head
//          arg match {
//            case _ if arg != null && arg.getClass == firstParamType =>
//              return method.invoke(driver, arg)
//            case argStep: Map[String, Any] =>
//              val r = runStep(argStep)
//              log.debug(r)
//              return method.invoke(driver, r)
//            case _ =>
//              log.error(s"$method ( $arg) not found")
//          }
        }
      })
    })
  }

  def getMethodName(step: Map[String, Any]): String = {
    step.keys.head
  }

  def getArgs(step: Map[String, Any]): Any = {
    step.values.head
  }

  def create(BDDDriver: String): Unit = {

    BDDDriver match {
      case "selenium" => {
        val driver = new SeleniumDDT()
        bddDriverList.append(driver)
      }
      case "mock" => {
        bddDriverList.append(new MockDDT)
      }
      case default => {
        log.warn(s"$default not found")
      }
    }
    if (bddDriverList.isEmpty) {
      bddDriverList.append(new MockDDT())
    }

  }
}
