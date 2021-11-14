package com.ceshiren.appcrawler.model

import com.ceshiren.appcrawler.model.ElementInfo

class URIElementFactory {

  def generateElementStore = new URIElementStore

  def generateElement = new URIElement()

  def generateElement(url: String, tag: String, className: String, id: String, name: String, text: String, instance: String, depth: String, valid: String, selected: String, xpath: String, ancestor: String, x: Int, y: Int, width: Int, height: Int, action: String) = new URIElement(url, tag, className, id, name, text, instance, depth, valid, selected, xpath, ancestor, x, y, width, height, action)

  def generateElement(map: Map[String, Any], url: String) = {
    new URIElement(map, url)
  }

  def generateElementInfo(): ElementInfo = {
    new ElementInfo()
  }
}