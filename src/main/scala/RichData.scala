import java.io.{StringWriter, ByteArrayInputStream}
import java.nio.charset.StandardCharsets
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPathConstants, XPathFactory, XPath}

import org.apache.xml.serialize.{XMLSerializer, OutputFormat}
import org.w3c.dom.{Node, Attr, NodeList, Document}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/3/26.
  */
object RichData {
  def toXML(raw: String): Document = {
    val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    val document: Document = builder.parse(new ByteArrayInputStream(raw.replaceAll("[\\x00-\\x1F]", "").getBytes(StandardCharsets.UTF_8)))
    document
  }

  def toPrettyXML(raw:String): String ={
    val document=toXML(raw)
    val format = new OutputFormat(document); //document is an instance of org.w3c.dom.Document
    format.setLineWidth(65)
    format.setIndenting(true)
    format.setIndent(2)
    val out = new StringWriter()
    val serializer = new XMLSerializer(out, format)
    serializer.serialize(document)
    out.toString
  }

  def parseXPath(xpath:String, pageDom:Document): List[Map[String, Any]] ={
    val nodeMap=mutable.Map[String, Any]()
    val nodesMap=ListBuffer[Map[String, Any]]()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    //val node=compexp.evaluate(pageDom)

    val node=if(xpath.matches("string(.*)")){
      compexp.evaluate(pageDom, XPathConstants.STRING)
    }else{
      compexp.evaluate(pageDom, XPathConstants.NODESET)
    }

    node match {
      case nodeList:NodeList=>{
        0 until nodeList.getLength foreach (i => {
          val node=nodeList.item(i)
          println(node)
          val path=ListBuffer[String]()
          //递归获取路径
          def getParent(node: Node): Unit ={
            println(node.getNodeName)
            path+=node.getNodeName.split('.').lastOption.getOrElse("_")
            if(node.getParentNode!=null){
              getParent(node.getParentNode)
            }
          }
          getParent(node)

          //支持导出单个字段
          nodeMap(node.getNodeName)=node.getNodeValue

          val nodeAttributes = node.getAttributes
          if(nodeAttributes!=null) {
            0 until nodeAttributes.getLength foreach (a => {
              val attr = nodeAttributes.item(a).asInstanceOf[Attr]
              nodeMap(attr.getName) = attr.getValue
            })
          }

          nodeMap("tag") = node.getNodeName
          nodeMap("uri") = path.reverse.mkString(".")

          //如果是android 转化为和iOS相同的结构
          if (!nodeMap.contains("name")) {
            nodeMap("name") = ""
            nodeMap("value") = ""
          }
          //name属性为android的resource-id
          if (nodeMap.contains("resource-id")) {
            //todo: /结尾的会被解释为/之前的内容
            val arr = nodeMap("resource-id").toString.split('/')
            if (arr.length == 1) {
              nodeMap("name") = ""
            } else {
              nodeMap("name") = nodeMap("resource-id").toString.split('/').last
            }
          }
          //value为android的text或者iOS的value
          if (nodeMap.contains("text")) {
            nodeMap("value") = nodeMap("text")
          }
          //loc为android坐标或者iOS路径
          if (nodeMap.contains("bounds")) {
            nodeMap("loc") = nodeMap("bounds")
          }
          if (nodeMap.contains("x")) {
            nodeMap("loc") = nodeMap("x") + "," + nodeMap("y")
          }
          if (nodeMap.contains("path")) {
            nodeMap("loc") = nodeMap("path")
          }

          nodesMap+=(nodeMap.toMap)
        })
      }
      case _ => {
        println("not node list")
        println(node)
      }
    }
    nodesMap.toList
  }

}
