package com.testerhome.appcrawler

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
object XPathUtil extends CommonLog {
  var xpathExpr=List("name", "label", "value", "resource-id", "content-desc", "class", "text", "index")

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
    var xpath = attributes.map(attribute => {
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
      //优先取content-desc
      if (newAttribute.getOrElse("content-desc", "") == newAttribute.getOrElse("resource-id", "")) {
        newAttribute = newAttribute - "resource-id"
      }

      var xpathSingle = newAttribute.map(kv => {
        //todo: appium的bug. 如果控件内有换行getSource会自动去掉换行. 但是xpath表达式里面没换行会找不到元素
        //todo: 需要帮appium打补丁

        kv._1 match {
          case "tag" => ""
          //case "index" => ""
          case "name" if kv._2.size>50 => ""
            //todo: 优化长文本的展示
          case "text" if newAttribute("tag").contains("Button")==false && kv._2.length>10 => {
            log.trace(kv)
            log.trace(newAttribute)
            ""
          }
          case key if xpathExpr.contains(key) && kv._2.nonEmpty => s"@${kv._1}=" + "\"" + kv._2.replace("\"", "\\\"") + "\""
          case _ => ""
        }
      }).filter(x => x.nonEmpty).mkString(" and ")

      //todo: 改进xpath算法，更灵活，更唯一，更简短
      //todo: macaca的source有问题
      xpathSingle = if (xpathSingle.isEmpty) {
        //s"/${attribute.getOrElse("class", attribute.getOrElse("tag", "*"))}"
        ""
      } else {
        s"//*[${xpathSingle}]"
      }
      xpathSingle
    }
    ).mkString("")
    return xpath
  }

  def getAttributesFromNode(node: Node): ListBuffer[Map[String, String]] ={
    val attributesList = ListBuffer[Map[String, String]]()
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
        attributesList += attributeMap
      }

      if (node.getParentNode != null) {
        getParent(node.getParentNode)
      }
    }
    getParent(node)
    //返回一个从root到leaf的属性列表
    return attributesList.reverse

  }


  def getAncestorFromNode(node: Node): Unit ={


  }
  def isMenuFromBrotherNode(node: Node): Boolean ={
    var b=false
    val nodeList=node.getParentNode.getChildNodes
    0 until(nodeList.getLength) foreach(i=>{
      if(b==false) {
        val attributes = nodeList.item(i).getAttributes
        if(attributes!=null) {
          0 until attributes.getLength foreach (i => {
            val kv = attributes.item(i).asInstanceOf[Attr]
            if (kv.getName == "selected" && kv.getValue() == "true") {
              b = true
            }
          })
        }
      }
    })
    b
  }


  def getNodeListFromXML(raw:String, xpath:String): AnyRef ={
    val pageDom=toDocument(raw)
    getNodeListFromXML(pageDom, xpath)
  }

  def getNodeListFromXML(pageDom:Document, xpath:String): AnyRef ={
    val nodesMap = ListBuffer[Map[String, Any]]()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    //val node=compexp.evaluate(pageDom)
    if (xpath.matches("string(.*)") || xpath.matches(".*/@[^/]*")) {
      compexp.evaluate(pageDom, XPathConstants.STRING)
    } else {
      compexp.evaluate(pageDom, XPathConstants.NODESET)
    }
  }


  def getNodeListFromXPath(xpath:String, pageDomString: String): List[Map[String, Any]] ={
    val pageDom=toDocument(pageDomString)
    getNodeListFromXPath(xpath, pageDom)
  }
  def getNodeListFromXPath(xpath: String, pageDom: Document): List[Map[String, Any]] = {

    val node=getNodeListFromXML(pageDom, xpath)

    val nodeMapList = ListBuffer[Map[String, Any]]()
    node match {
      case nodeList: NodeList => {
        0 until nodeList.getLength foreach (i => {
          val nodeMap = mutable.Map[String, Any]()
          //初始化必须的字段
          nodeMap("name") = ""
          nodeMap("value") = ""
          nodeMap("label") = ""
          nodeMap("x")="0"
          nodeMap("y")="0"
          nodeMap("width")="0"
          nodeMap("height")="0"

          val node = nodeList.item(i)
          //如果node为.可能会异常. 不过目前不会
          nodeMap("tag") = node.getNodeName


          val attributesList=getAttributesFromNode(node)
          //必须在xpath前面
          nodeMap("depth") = attributesList.size

          //todo: 改进算法
          //nodeMap("menu") = isMenuFromBrotherNode(node)
          nodeMap("ancestor")=attributesList.map(_.get("tag").get).mkString("/")
          nodeMap("xpath") = getXPathFromAttributes(attributesList)

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
            nodeMap("name") = nodeMap("resource-id").toString
          }
          if (nodeMap.contains("text")) {
            nodeMap("value") = nodeMap("text")
          }
          if (nodeMap.contains("content-desc")) {
            nodeMap("label") = nodeMap("content-desc")
          }
          //为了加速android定位
          if (nodeMap.contains("instance")) {
            nodeMap("instance") = nodeMap("instance")
          }
          //默认true
          nodeMap("valid")= nodeMap.getOrElse("visible", "true") == "true" &&
            nodeMap.getOrElse("enabled", "true") == "true" &&
            nodeMap.getOrElse("valid", "true") == "true"

          //android
          if(nodeMap.contains("bounds")){
            val rect=nodeMap("bounds").toString.split("[^0-9]+").takeRight(4)
            nodeMap("x")=rect(0).toInt
            nodeMap("y")=rect(1).toInt
            nodeMap("width")=rect(2).toInt-rect(0).toInt
            nodeMap("height")=rect(3).toInt-rect(1).toInt
          }


          if (nodeMap("xpath").toString.nonEmpty && nodeMap("value").toString().size<50) {
            nodeMapList += (nodeMap.toMap)
          }
        } )
      }
      case attr:String => {
        //如果是提取xpath的属性值, 就返回一个简单的结构
        nodeMapList+=Map("attribute"->attr)
      }
    }
    nodeMapList.toList
  }

  def getNodeListByKey(key:String, currentPageSource:String): List[Map[String, Any]] ={
    val currentPageDom=toDocument(currentPageSource)
    getNodeListByKey(key, currentPageDom)
  }
  def getNodeListByKey(key:String, currentPageDom: Document): List[Map[String, Any]] ={
    key match {
      //xpath
      case xpath if Array("/.*", "\\(.*", "string\\(/.*\\)").exists(xpath.matches(_)) => {
        getNodeListFromXPath(xpath, currentPageDom)
      }
      case regex if regex.contains(".*") || regex.startsWith("^")  => {
        getNodeListFromXPath("//*", currentPageDom).filter(m=>{
          m("name").toString.matches(regex) ||
            m("label").toString.matches(regex) ||
            m("value").toString.matches(regex)
        })
      }
      case str: String => {
        getNodeListFromXPath("//*", currentPageDom).filter(m=>{
          m("name").toString.contains(str) ||
            m("label").toString.contains(str) ||
            m("value").toString.contains(str)
        })
      }
    }
  }

}