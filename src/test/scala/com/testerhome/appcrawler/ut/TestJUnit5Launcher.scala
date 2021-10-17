package com.ceshiren.appcrawler.ut

import org.junit.platform.engine.discovery.DiscoverySelectors._
import org.junit.platform.engine.discovery.ClassNameFilter._
import org.scalatest.FunSuite
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener


class TestJUnit5Launcher extends FunSuite{
  test("xxxx"){
    val request = LauncherDiscoveryRequestBuilder.request.selectors(
      selectPackage("com.ceshiren.appcrawler.ut"),
      selectClass(classOf[TestJUnit5])).build()
      //.filters(includeClassNamePatterns("Test.*")).build

    val launcher = LauncherFactory.create

    // Register a listener of your choice
    val listener = new SummaryGeneratingListener
    launcher.registerTestExecutionListeners(listener)

    val roots=launcher.discover(request).getRoots()
    val iter=roots.iterator()
    while(iter.hasNext){
      val root=iter.next()
      println(root)
      val iter2=launcher.discover(request).getDescendants(root).iterator()
      while(iter2.hasNext){
        println(iter2.next())
      }
    }
    println(launcher.execute(request))
  }


}
