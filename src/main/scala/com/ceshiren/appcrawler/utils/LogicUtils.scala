package com.ceshiren.appcrawler.utils

import com.ceshiren.appcrawler.utils.Log.log

import java.util.concurrent.{Callable, Executors, TimeUnit, TimeoutException}
import scala.util.{Failure, Success, Try}

object LogicUtils {

  def handleException(e: Throwable): Unit = {
    var exception = e
    do {
      log.error(exception.getLocalizedMessage)
      exception.getStackTrace.foreach(log.error)
      if (exception.getCause != null) {
        log.error("find more cause")
      } else {
        log.error("exception finish")
      }
      exception = exception.getCause
    } while (exception != null)
  }

  def tryAndCatch[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        log.info("retry execute success")
        Some(v)
      }
      case Failure(e) => {
        handleException(e)
        None
      }
    }
  }

  def retryToSuccess(timeoutMS: Int, intervalMS: Int = 500, name: String = "")(callback: => Boolean): Try[Boolean] = {
    val start = System.currentTimeMillis()
    var r = false
    var error: Throwable = new Exception()
    do {
      Try(callback) match {
        case Success(value) => {
          r = value
        }
        case Failure(exception) => {
          error = exception
        }
      }
      Thread sleep (intervalMS)
      log.info(s"name=${name} wait")
    } while (System.currentTimeMillis() - start < timeoutMS && !r)
    log.info(s"name=${name} finish")
    if (r) {
      Success(r)
    } else {
      Failure(error)
    }


  }

  def asyncTask[T](timeout: Int = 30, name: String = "", needThrow: Boolean = false)(callback: => T): Either[T, Throwable] = {
    //todo: 异步线程消耗资源厉害，需要改进
    val start = System.currentTimeMillis()
    Try({
      val task = Executors.newSingleThreadExecutor().submit(new Callable[T]() {
        def call(): T = {
          callback
        }
      })
      if (timeout < 0) {
        task.get()
      } else {
        task.get(timeout, TimeUnit.SECONDS)
      }

    }) match {
      case Success(v) => {
        val end = System.currentTimeMillis()
        val use = (end - start) / 1000d
        if (use >= 0.5) {
          log.info(s"use time $use seconds name=${name} result=success")
        }
        Left(v)
      }
      case Failure(e) => {
        val end = System.currentTimeMillis()
        val use = (end - start) / 1000d
        if (use >= 1) {
          log.info(s"use time $use seconds name=${name} result=error")
        }
        if (needThrow) {
          throw e
        }
        e match {
          case e: TimeoutException => {
            log.error(s"${timeout} seconds timeout")
          }
          case _ => {
            handleException(e)
          }
        }
        Right(e)
      }
    }
  }

}
