package com.xueqiu.qa.appcrawler

import java.io.{ByteArrayInputStream, StringWriter}
import java.nio.charset.StandardCharsets
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPath, XPathConstants, XPathFactory}

import com.sun.org.apache.xml.internal.serialize.{XMLSerializer, OutputFormat}

//import org.apache.xml.serialize.{OutputFormat, XMLSerializer}
import org.w3c.dom.{Attr, Document, Node, NodeList}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/3/26.
  */
object RichData extends CommonLog {
  def toXML(raw: String): Document = {
    val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    val document: Document = builder.parse(new ByteArrayInputStream(raw.replaceAll("[\\x00-\\x1F]", "").getBytes(StandardCharsets.UTF_8)))
    document
  }

  def toPrettyXML(raw: String): String = {
    val document = toXML(raw)
    val format = new OutputFormat(document); //document is an instance of org.w3c.dom.Document
    format.setLineWidth(65)
    format.setIndenting(true)
    format.setIndent(2)
    val out = new StringWriter()
    val serializer = new XMLSerializer(out, format)
    serializer.serialize(document)
    out.toString
  }

  /**
    * 从属性中获取xpath的唯一表达式
    *
    * @param attributes
    * @return
    */
  def getXPathFromAttributes(attributes: ListBuffer[Map[String, String]]): String = {
    var xpath = attributes.reverse.takeRight(2).map(attribute => {
      var newAttribute = attribute
      //如果有值就不需要path了, 基本上两层xpath定位即可唯一
      List("name", "label", "value", "resource-id", "content-desc", "index", "text").foreach(key => {
        if (newAttribute.getOrElse(key, "").isEmpty) {
          newAttribute = newAttribute - key
        } else {
          newAttribute = newAttribute - "path"
        }
      })

      //如果label和name相同且非空 取一个即可
      if (newAttribute.getOrElse("name", "") == newAttribute.getOrElse("label", "")) {
        newAttribute = newAttribute - "name"
      }
      if (newAttribute.getOrElse("content-desc", "") == newAttribute.getOrElse("resource-id", "")) {
        newAttribute = newAttribute - "content-desc"
      }

      var xpathSingle = newAttribute.map(kv => {
        //todo: appium的bug. 如果控件内有换行getSource会自动去掉换行. 但是xpath表达式里面没换行会找不到元素
        //todo: 需要帮appium打补丁
        if (kv._1 != "tag") {
          if (kv._1 == "name" && kv._2.size > 20) {
            log.trace(s"name size too long ${kv._2.size}>20")
            ""
          }
          else if (kv._1 == "text" && kv._2.size > 20) {
            log.trace(s"text size too long ${kv._2.size}>20")
            ""
          }
          else {
            s"@${kv._1}=" + "\"" + kv._2.replace("\"", "\\\"") + "\""
          }
        } else {
          ""
        }
      }).filter(x => x.nonEmpty).mkString(" and ")

      xpathSingle = if (xpathSingle.isEmpty) {
        s"/${attribute("tag")}"
      } else {
        s"/${attribute("tag")}[${xpathSingle}]"
      }
      xpathSingle
    }
    ).mkString("")
    if (xpath.isEmpty) {
      log.trace(attributes)
    } else {
      xpath = "/" + xpath
    }
    return xpath
  }


  def getListFromXPath(xpath: String, pageDom: Document): List[Map[String, Any]] = {
    val nodesMap = ListBuffer[Map[String, Any]]()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    //val node=compexp.evaluate(pageDom)

    val node = if (xpath.matches("string(.*)")) {
      compexp.evaluate(pageDom, XPathConstants.STRING)
    } else {
      compexp.evaluate(pageDom, XPathConstants.NODESET)
    }

    node match {
      case nodeList: NodeList => {
        0 until nodeList.getLength foreach (i => {
          val nodeMap = mutable.Map[String, Any]()
          val node = nodeList.item(i)
          nodeMap("tag") = node.getNodeName
          val path = ListBuffer[Map[String, String]]()
          //递归获取路径,生成可定位的xpath表达式
          def getParent(node: Node): Unit = {
            if (node.hasAttributes) {
              val attributes = node.getAttributes
              var attributeMap = Map[String, String]()

              0 until attributes.getLength foreach (i => {
                val kv = attributes.item(i).asInstanceOf[Attr]
                if (List("name", "label", "path", "resource-id", "content-desc", "index", "text").contains(kv.getName)
                  && kv.getValue.nonEmpty
                ) {
                  attributeMap ++= Map(kv.getName -> kv.getValue)
                }
              })
              attributeMap ++= Map("tag" -> node.getNodeName)
              path += attributeMap
            }
            if (node.getParentNode != null) {
              getParent(node.getParentNode)
            }
          }
          getParent(node)
          nodeMap("xpath") = getXPathFromAttributes(path)

          //支持导出单个字段
          nodeMap(node.getNodeName) = node.getNodeValue
          //获得所有节点属性
          val nodeAttributes = node.getAttributes
          if (nodeAttributes != null) {
            0 until nodeAttributes.getLength foreach (a => {
              val attr = nodeAttributes.item(a).asInstanceOf[Attr]
              nodeMap(attr.getName) = attr.getValue
            })
          }

          //保持根元素兼容
          if (nodeMap.contains("resource-id") == false && nodeMap.contains("name") == false) {
            nodeMap("name") = ""
            nodeMap("value") = ""
            nodeMap("label") = ""
          }
          //todo: 支持selendroid
          //如果是android 转化为和iOS相同的结构
          //name=resource-id label=content-desc value=text
          if (nodeMap.contains("resource-id")) {
            //todo: /结尾的会被解释为/之前的内容
            val arr = nodeMap("resource-id").toString.split('/')
            if (arr.length == 1) {
              nodeMap("name") = ""
            } else {
              nodeMap("name") = nodeMap("resource-id").toString.split('/').last
            }
          }
          if (nodeMap.contains("text")) {
            nodeMap("value") = nodeMap("text")
          }
          if (nodeMap.contains("content-desc")) {
            nodeMap("label") = nodeMap("content-desc")
          }

          if (nodeMap("xpath").toString.nonEmpty) {
            nodesMap += (nodeMap.toMap)
          } else {
            log.trace(s"xpath error skip ${nodeMap}")
          }
        } )
      }
      case _ => {
        log.trace("not node list")
        log.trace(node)
      }
    }
    nodesMap.toList
  }

}