/*
package com.testerhome.appcrawler.plugin

import com.sun.jdi.event._
import com.sun.jdi.request.{EventRequest, EventRequestManager}
import com.sun.jdi.{Bootstrap, ReferenceType, VirtualMachine}
import com.sun.tools.jdi.SocketAttachingConnector
import com.testerhome.appcrawler.CommonLog
import com.testerhome.appcrawler.CommonLog

import collection.JavaConversions._

/**
  * Created by seveniruby on 16/4/24.
  */


class AndroidTrace extends CommonLog {
  var eventRequestManager: EventRequestManager = _
  var vm: VirtualMachine = _

  def getConnections(): Unit = {
    val sac = Bootstrap.virtualMachineManager().attachingConnectors().toArray.filter(_.isInstanceOf[SocketAttachingConnector]).head.asInstanceOf[SocketAttachingConnector]
    log.info(sac)
    log.info(sac.defaultArguments())
    val arguments = sac.defaultArguments()
    arguments.get("hostname").setValue("127.0.0.1")
    arguments.get("port").setValue("8000")
    log.info(arguments)
    vm = sac.attach(arguments)
    val process = vm.process()
    log.info(process)
    vm.allClasses().map(_.name().split('.').take(4).mkString(".")).distinct.foreach(log.info)
    vm.setDebugTraceMode(VirtualMachine.TRACE_EVENTS)
    registEvent()
    eventLoop()

  }

  def eventLoop(): Unit = {
    val queue = vm.eventQueue()

    //vm.resume()

    var eventIndex = 0
    while (true) {
      val eventSet = queue.remove()
      eventSet.foreach(e => {
        eventIndex += 1
        e match {
          case e: VMStartEvent => {
            log.info(e)
          }
          case e: BreakpointEvent => {
            log.info("breakpoint catch")
            log.info(e.thread().status())
            val frame = e.thread().frame(0)
            log.info("frames")
            log.info(e.thread().frames().map(f=>s"${f.location().declaringType().name()}:${f.location().lineNumber()}:${f.location().method().name()}").mkString("\n"))
              log.info("arguments")
              frame.getArgumentValues.foreach(av=>log.info(av))
            log.info("value")
            frame.visibleVariables().foreach(v => log.info(s"${v}=${frame.getValue(v)}"))
          }
          /*
          case e: MethodEntryEvent => {
            log.info("method show")
            log.info(e.thread().status())
            if(e.thread().status()!=Thread.State.TIMED_WAITING && e.thread().status()!=Thread.State.TIMED_WAITING) {

              //log.info(e.method().allLineLocations())
              //log.info(e.method().arguments())
              //log.info(e.method().variables())
              log.info(e.thread().frames().map(_.location()))
              //log.info(e.method().arguments())
              log.info(e.toString)
              //log.info(e.method().arguments())
            }
          }*/
          case e: ClassPrepareEvent => {
            log.info(s"class prepare ${e.referenceType().getClass}")
          }
          case e: VMDeathEvent => {
            log.info("quit")
            System.exit(0)
          }
          case _ => {
          }
        }
      })

      eventSet.resume()

    }

  }

  def registEvent(): Unit = {
    val suspend = EventRequest.SUSPEND_EVENT_THREAD
    eventRequestManager = vm.eventRequestManager()
    /*
        val methodEntry = eventRequestManager.createMethodEntryRequest()
        methodEntry.setSuspendPolicy(suspend)
        methodEntry.addClassFilter("org.json.JSONObject")
        methodEntry.enable()
    */

    val breakpoints=List(
      "com.google.gson.JsonObject:get",
      "com.google.gson.JsonObject:getAs.*",
      "com.google.gson.JsonObject:get.*",
      ".*json.*:isJsonNull",
      ".*gson.*:isJsonNull",
      "android.view.View:onClick",
      ".*TextView.*:onClick",
      "android.*:onClick"
    )
    val blackList=List("getAsJsonPrimitive", ".*Class.*", "getAsJsonObject")
    breakpoints.foreach(b=>{
      val classMatcher=b.split(":").head
      val methodMatcher=b.split(":").last
      log.info(s"${classMatcher} ${methodMatcher}")
      vm.allClasses().filter(_.name().matches(classMatcher))
        .flatMap(_.allMethods()).filter(m=>m.name().matches(methodMatcher) && blackList.map(m.name().matches(_)).contains(true)==false)
        .flatMap(_.allLineLocations())
        .distinct.foreach(location => {
        log.info(s"set break on ${location.method().name()} ${location}")
        val bp = eventRequestManager.createBreakpointRequest(location)
        bp.enable()
      })
    })
    log.info("all breakpoints enable")
    /*
    val methodExit = eventRequestManager.createMethodExitRequest()
    methodExit.setSuspendPolicy(suspend)
    methodExit.enable()

    val classPrepare = eventRequestManager.createClassPrepareRequest()
    classPrepare.setSuspendPolicy(suspend)
    classPrepare.enable()
    */

    val tsr = eventRequestManager.createThreadStartRequest();
    tsr.setSuspendPolicy(suspend);
    tsr.enable();
    // 注册线程结束事件
    val tdr = eventRequestManager.createThreadDeathRequest();
    tdr.setSuspendPolicy(suspend);
    tdr.enable()
  }

}
*/
