package com.ceshiren.appcrawler.utils

import com.ceshiren.appcrawler.utils.Log.log
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.w3c.dom.{Attr, Document, Node, NodeList}
import org.xml.sax.InputSource

import java.io.StringReader
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPath, XPathConstants, XPathFactory}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


/**
  * Created by seveniruby on 16/3/26.
  */
object XPathUtil {
  var xpathExpr = List("name", "label", "value", "resource-id", "content-desc", "class", "text", "index")
  var tagList = List("class", "resource-id", "name", "content-desc", "label")

  val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
  val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
  val mapper = new XmlMapper

  def toDocument(raw: String): Document = {
    //todo: appium有bug, 会返回&#非法字符. 需要给appium打补丁
    //todo: 解决html的兼容问题 SAXParseException: 注释中不允许出现字符串 "--"
    val rawFormat = raw.replaceAll("[\\x00-\\x1F]", "")
      .replaceAll("&", "_")
    try {
      val document: Document = builder.parse(
        new InputSource(
          new StringReader(rawFormat)))
      document
    } catch {
      case ex: Exception => {
        log.error(rawFormat)
        scala.reflect.io.File("/tmp/1.xml").writeAll(rawFormat)
        throw ex
      }
    }
  }

  //todo: xml中有$b
  def toPrettyXML(raw: String): String = {
    val xmlNode=mapper.readTree(raw)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    mapper.writeValueAsString(xmlNode)


/*    //done: android page source不能格式化，但是普通的xml可以, android page source在1.9后多了一些多余的空格
    val document = toDocument(raw.trim.replaceAll("> *<", "><"))
    //done: 不支持java10, use Xalan replace
    val transformerFactory = TransformerFactory.newInstance
    val transformer = transformerFactory.newTransformer
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    val source = new DOMSource(document)
    val strWriter = new StringWriter
    val result = new StreamResult(strWriter)

    transformer.transform(source, result)

    return strWriter.getBuffer.toString*/

  }

  def setXPathExpr(expr: List[String]): Unit = {
    xpathExpr = expr
  }
  def setTagList(expr: List[String]): Unit = {
    tagList = expr
  }

  /**
    * 从属性中获取xpath的唯一表达式
    *
    * @param attributes
    * @return
    */
  def getXPathFromAttributes(attributes: ListBuffer[Map[String, String]], includeList: List[String]): String = {
    var index = attributes.size
    val xpath = attributes.map(attribute => {
      index -= 1
      var newAttribute = attribute
      //如果有值就不需要path了, 基本上两层xpath定位即可唯一
      includeList.foreach(key => {
        if (newAttribute.getOrElse(key, "").isEmpty) {
          newAttribute = newAttribute - key
        } else {
          newAttribute = newAttribute - "path"
        }
      })

      val tag = if (includeList.contains("name()")) {
        newAttribute.getOrElse("name()", "*")
      } else {
        "*"
      }
      val xpathAttributes = newAttribute.map(kv => {
        //todo: appium的bug. 如果控件内有换行getSource会自动去掉换行. 但是xpath表达式里面没换行会找不到元素
        //todo: 需要帮appium打补丁

        kv._1 match {
          //不同的实现name()可能会有差异，所以不考虑这个，尽量使用class属性
          case "name()" => ""
          case "class" if kv._2.contains("hierarchy")=> ""
          case "innerText" => {
            if (includeList.contains("innerText") && index == 0) {
              s"contains(text()," + "'" + kv._2.trim.take(20).replace("\"", "\\\"") + "')"
            } else {
              ""
            }

          }
          //case "index" => ""
          case "name" if kv._2.size > 50 => ""
          //todo: 优化长文本的展示
          case "text" if !newAttribute("name()").contains("Button") && kv._2.length > 10 => {
            ""
          }
          case key if includeList.contains(key) && kv._2.nonEmpty =>
            s"@${kv._1}=" + "'" + kv._2.replace("\"", "\\\"") + "'"
          case _ => ""
        }
      }).filter(x => x.nonEmpty).mkString(" and ")

      //todo: 改进xpath算法，更灵活，更唯一，更简短
      //todo: macaca的source有问题
      if (xpathAttributes.isEmpty) {
        ""
      } else {
        s"//${tag}[${xpathAttributes}]"
      }
    }
    )

    //    var shortXPath = false
    //    xpath.indices.reverse.foreach(i => {
    //      if (!shortXPath) {
    //        if (xpath(i).contains(" and ")) {
    //          shortXPath = true
    //        }
    //      } else {
    //        xpath.remove(i)
    //      }
    //    })

    xpath.mkString("")
  }

  def getAttributesFromNode(node: Node): ListBuffer[Map[String, String]] = {
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
        attributeMap ++= Map("name()" -> node.getNodeName)
        attributeMap ++= Map("innerText" -> node.getTextContent.trim)
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


  def getAncestorFromNode(node: Node): Unit = {


  }

  def isMenuFromBrotherNode(node: Node): Boolean = {
    var b = false
    val nodeList = node.getParentNode.getChildNodes
    0 until (nodeList.getLength) foreach (i => {
      if (b == false) {
        val attributes = nodeList.item(i).getAttributes
        if (attributes != null) {
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

  def getNodeListFromXML(pageDom: Document, xpath: String): AnyRef = {
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    var r: AnyRef = None
    //val node=compexp.evaluate(pageDom)
    if (xpath.matches("string(.*)") || xpath.matches(".*/@[^/]*")) {
      r = compexp.evaluate(pageDom, XPathConstants.STRING)
    } else {
      r = compexp.evaluate(pageDom, XPathConstants.NODESET)
    }
    r
  }


  def getNodeListByXPath(xpath: String, pageDomString: String): List[Map[String, Any]] = {
    val pageDom = toDocument(pageDomString)
    getNodeListByXPath(xpath, pageDom)
  }

  def getNodeListByXPath(xpath: String, pageDom: Document): List[Map[String, Any]] = {
    val node = getNodeListFromXML(pageDom, xpath)
    val nodeMapList = ListBuffer[Map[String, Any]]()
    node match {
      case nodeList: NodeList => {
        log.trace("nodeList length {} with {}", nodeList.getLength, xpath)
        0 until nodeList.getLength foreach (i => {
          val nodeMap = mutable.Map[String, Any]()
          //初始化必须的字段
          nodeMap("name") = ""
          nodeMap("value") = ""
          nodeMap("label") = ""
          nodeMap("x") = "0"
          nodeMap("y") = "0"
          nodeMap("width") = "0"
          nodeMap("height") = "0"


          val node = nodeList.item(i)
          //如果node为.可能会异常. 不过目前不会
          nodeMap("name()") = node.getNodeName
          nodeMap("innerText") = node.getTextContent.trim

          //todo: html有label属性会导致label被覆盖
          //支持导出单个字段
          //nodeMap(node.getNodeName) = node.getNodeValue

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

          if (nodeMap.contains("content-desc")) {
            nodeMap("label") = nodeMap("content-desc")
          }

          if (nodeMap.contains("text")) {
            nodeMap("value") = nodeMap("text")
          }

          if (nodeMap.contains("latest")) {
            nodeMap("latest") = nodeMap("latest")
          }

          //selenium
          if (nodeMap.contains("href")) {
            if (nodeMap.contains("id")) {
              nodeMap("name") = nodeMap("id")
            }
            if (nodeMap.contains("name")) {
              nodeMap("label") = nodeMap("name")
            }
            nodeMap("value") = nodeMap("innerText")
          }


          val attributesList = getAttributesFromNode(node)
          //必须在xpath前面
          nodeMap("depth") = attributesList.size

          //todo: 改进算法
          //nodeMap("menu") = isMenuFromBrotherNode(node)

          //不同id的祖先可以允许分别遍历，不计算到相同的tagLimit里
          nodeMap("ancestor") = getXPathFromAttributes(attributesList, tagList)

          nodeMap("xpath") = getXPathFromAttributes(attributesList, xpathExpr)


          //为了加速android定位
          if (nodeMap.contains("instance")) {
            nodeMap("instance") = nodeMap("instance")
          }
          //默认true
          nodeMap("valid") = nodeMap.getOrElse("visible", "true") == "true" &&
            nodeMap.getOrElse("enabled", "true") == "true" &&
            nodeMap.getOrElse("valid", "true") == "true"

          //android
          if (nodeMap.contains("bounds")) {
            val rect = nodeMap("bounds").toString.split("[^0-9]+").takeRight(4)
            nodeMap("x") = rect(0).toInt
            nodeMap("y") = rect(1).toInt
            nodeMap("width") = rect(2).toInt - rect(0).toInt
            nodeMap("height") = rect(3).toInt - rect(1).toInt
          }

          if (nodeMap("xpath").toString.nonEmpty) {
            nodeMapList += (nodeMap.toMap)
          }
        })
      }
      case attr: String => {
        //如果是提取xpath的属性值, 就返回一个简单的结构
        nodeMapList += Map("attribute" -> attr)
      }
    }
    log.trace("filted node list length {}", nodeMapList.length)
    nodeMapList.toList
  }

  def getNodeListByKey(key: String, currentPageSource: String): List[Map[String, Any]] = {
    val currentPageDom = toDocument(currentPageSource)
    getNodeListByKey(key, currentPageDom)
  }

  def getNodeListByKey(key: String, currentPageDom: Document): List[Map[String, Any]] = {
    key match {
      //xpath
      case xpath if Array("/.*", "\\(.*", "string\\(/.*\\)").exists(xpath.matches) => {
        getNodeListByXPath(xpath, currentPageDom)
      }
      case regex if regex.contains(".*") || regex.startsWith("^") => {
        getNodeListByXPath("//*", currentPageDom).filter(m => {
          m("name").toString.matches(regex) ||
            m("label").toString.matches(regex) ||
            m("value").toString.matches(regex)
        })
      }
      case str: String => {
        getNodeListByXPath("//*", currentPageDom).filter(m => {
          m("name").toString.contains(str) ||
            m("label").toString.contains(str) ||
            m("value").toString.contains(str)
        })
      }
    }
  }

}
