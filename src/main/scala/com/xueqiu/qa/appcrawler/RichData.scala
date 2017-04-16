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
  var xpathExpr=List("name", "label", "value", "resource-id", "content-desc", "index", "text")

  def toDocument(raw: String): Document = {
    val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    //todo: appium有bug, 会返回&#非法字符. 需要给appium打补丁
    val document: Document = builder.parse(
      new ByteArrayInputStream(
        //todo: 替换可能存在问题
        raw.replaceAll("[\\x00-\\x1F]", "").replace("&#", xml.Utility.escape("&#")).getBytes(StandardCharsets.UTF_8)))
    document
  }

  def toPrettyXML(raw: String): String = {
    val document = toDocument(raw)
    val format = new OutputFormat(document); //document is an instance of org.w3c.dom.Document
    format.setLineWidth(65)
    format.setIndenting(true)
    format.setIndent(2)
    val out = new StringWriter()
    val serializer = new XMLSerializer(out, format)
    serializer.serialize(document)
    out.toString
  }

  def setXPathExpr(expr:List[String]): Unit ={
    xpathExpr=expr
  }
  /**
    * 从属性中获取xpath的唯一表达式
    *
    * @param attributes
    * @return
    */
  def getXPathFromAttributes(attributes: ListBuffer[Map[String, String]]): String = {
    var xpath = attributes.takeRight(4).map(attribute => {
      var newAttribute = attribute
      //如果有值就不需要path了, 基本上两层xpath定位即可唯一
      xpathExpr.foreach(key => {
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

        kv._1 match {
          case "tag" => ""
          case "index" => ""
          case "name" if kv._2.size>50 => ""
          case "text" if newAttribute("tag").contains("Button")==false => ""
          case key if xpathExpr.contains(key) && kv._2.nonEmpty => s"@${kv._1}=" + "\"" + kv._2.replace("\"", "\\\"") + "\""
          case _ => ""
        }
        /*
        if (kv._1 != "tag") {
          if (kv._1 == "name" && kv._2.size > 50) {
            log.trace(s"name size too long ${kv._2.size}>20")
            ""
          }
          //只有按钮才需要记录文本, 文本框很容易变化, 不需要记录
          else if (kv._1 == "text" && kv._2.size > 10 && newAttribute("tag").contains("Button") ) {
            log.trace(s"text size too long ${kv._2.size}>10")
            s"contains(@text, '${kv._2.split("&")(0).take(10)}')"
          }
          else {
            s"@${kv._1}=" + "\"" + kv._2.replace("\"", "\\\"") + "\""
          }
        } else {
          ""
        }
        */
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

  def getAttributesFromNode(node: Node): ListBuffer[Map[String, String]] ={
    val path = ListBuffer[Map[String, String]]()
    //递归获取路径,生成可定位的xpath表达式
    def getParent(node: Node): Unit = {
      if (node.hasAttributes) {
        val attributes = node.getAttributes
        var attributeMap = Map[String, String]()

        0 until attributes.getLength foreach (i => {
          val kv = attributes.item(i).asInstanceOf[Attr]
          attributeMap ++= Map(kv.getName -> kv.getValue)
        })
        attributeMap ++= Map("tag" -> node.getNodeName)
        path += attributeMap
      }
      if (node.getParentNode != null) {
        getParent(node.getParentNode)
      }
    }
    getParent(node)
    //返回一个从root到leaf的属性列表
    return path.reverse

  }


  def getListFromXPath(xpath: String, pageDom: Document): List[Map[String, Any]] = {
    val nodesMap = ListBuffer[Map[String, Any]]()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    //val node=compexp.evaluate(pageDom)
    val node = if (xpath.matches("string(.*)") || xpath.matches(".*/@[^/]*")) {
      compexp.evaluate(pageDom, XPathConstants.STRING)
    } else {
      compexp.evaluate(pageDom, XPathConstants.NODESET)
    }

    node match {
      case nodeList: NodeList => {
        0 until nodeList.getLength foreach (i => {
          val nodeMap = mutable.Map[String, Any]()
          //初始化必须的字段
          nodeMap("name") = ""
          nodeMap("value") = ""
          nodeMap("label") = ""

          val node = nodeList.item(i)
          //如果node为.可能会异常. 不过目前不会
          nodeMap("tag") = node.getNodeName
          val path=getAttributesFromNode(node)
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

          if (nodeMap("xpath").toString.nonEmpty && nodeMap("value").toString().size<50) {
            nodesMap += (nodeMap.toMap)
          } else {
            log.trace(s"xpath error skip ${nodeMap}")
          }
        } )
      }
      case attr:String => {
        //如果是提取xpath的属性值, 就返回一个简单的结构
        nodesMap+=Map("attribute"->attr)
      }
    }
    nodesMap.toList
  }

}