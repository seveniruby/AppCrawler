package com.ceshiren.appcrawler.ut

import org.junit.jupiter.api.Test

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.shell.{ILoop, ShellConfig}
import scala.Serializable

class TestShell {

  @Test
  def testDemo(): Unit ={
    val settings = new Settings {
      usejavacp.value = true
      deprecation.value = true
    }
    val config = ShellConfig(settings)
    new ILoop(config).run(settings)
  }

}
