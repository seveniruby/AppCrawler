package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.utils.Log
import com.ceshiren.appcrawler.utils.LogicUtils.retryToSuccess
import org.junit.jupiter.api.Test

import scala.util.{Failure, Success, Try}

class RequestsTest {
  @Test
  def get(): Unit ={
    val session=requests.Session()
//    while(Try(session.get("http://127.0.0.1:7778/ping").text()).isFailure){
//      Thread.sleep(500)
//      Log.log.info("wait")
//    }
//    Log.log.info("success")

    Log.log.info(retryToSuccess(timeoutMS = 6000)(session.get("http://127.0.0.1:7778/ping").text()=="pong"))
  }

}
