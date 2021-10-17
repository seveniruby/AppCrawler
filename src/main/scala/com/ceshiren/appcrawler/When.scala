package com.ceshiren.appcrawler

case class When(xpath: String = null,
                action: String = null,
                actions: List[String] = List[String]()
               )
