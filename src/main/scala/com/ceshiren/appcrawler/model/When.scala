package com.ceshiren.appcrawler.model

case class When(xpath: String = null,
                action: String = null,
                actions: List[String] = List[String]()
               )
