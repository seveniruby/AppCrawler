package com.ceshiren.appcrawler.core

import com.fasterxml.jackson.core.`type`.TypeReference

object Status extends Enumeration {
  type Status = Value
  val READY = Value
  val CLICKED = Value
  val SKIPPED = Value
}


class StatusType extends TypeReference[Status.type]
