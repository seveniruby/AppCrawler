package com.xueqiu.qa.appcrawler.plugin

import com.sun.jdi.event.{VMDeathEvent, ClassPrepareEvent, MethodEntryEvent, VMStartEvent}
import com.sun.jdi.request.{EventRequestManager, EventRequest}
import com.sun.jdi.{VirtualMachine, ReferenceType, Bootstrap}
import com.sun.tools.jdi.SocketAttachingConnector
import com.xueqiu.qa.appcrawler.CommonLog
import collection.JavaConversions._

/**
  * Created by seveniruby on 16/4/24.
  */


class AndroidTrace extends CommonLog {
  var eventRequestManager : EventRequestManager = _
  var vm : VirtualMachine = _
  def getConnections(): Unit = {
    val sac = Bootstrap.virtualMachineManager().attachingConnectors().toArray.filter(_.isInstanceOf[SocketAttachingConnector]).head.asInstanceOf[SocketAttachingConnector]
    log.info(sac)
    log.info(sac.defaultArguments())
    val arguments = sac.defaultArguments()
    arguments.get("hostname").setValue("127.0.0.1")
    arguments.get("port").setValue("8800")
    log.info(arguments)
    vm = sac.attach(arguments)
    val process=vm.process()
    log.info(process)
    vm.setDebugTraceMode(VirtualMachine.TRACE_EVENTS)
    registEvent()
    eventLoop()

  }

  def eventLoop(): Unit ={
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
          case e: MethodEntryEvent => {
            log.info("method show")
            log.info(e.method().getClass.getName)
            //log.info(e.method().arguments())
            log.info(e.toString)
            //log.info(e.method().arguments())
          }
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

        eventSet.resume()
      })

    }

  }
  def registEvent(): Unit ={
    val suspend=EventRequest.SUSPEND_EVENT_THREAD
    eventRequestManager = vm.eventRequestManager()
    val methodEntry = eventRequestManager.createMethodEntryRequest()
    methodEntry.setSuspendPolicy(suspend)
    //methodEntry.addClassFilter("com.xueqiu.qa.appcrawler.AppCrawler$")
    methodEntry.enable()

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
    tdr.enable();



  }

}
