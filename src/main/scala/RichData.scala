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
object RichData extends CommonLog{
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
          val path=ListBuffer[String]()
          //递归获取路径,生成可定位的xpath表达式
          def getParent(node: Node): Unit ={

            if(node.hasAttributes){
              val attributes=node.getAttributes
              val xpath=ListBuffer[String]()
              0 until attributes.getLength foreach(i=>{
                val kv=attributes.item(i).asInstanceOf[Attr]
                if(List("name", "path", "resource-id", "content-desc", "index").contains(kv.getName) &&
                  kv.getValue.nonEmpty
                ){
                  //appium的bug. 如果控件内有换行getSource会自动去掉换行. 但是xpath表达式里面没换行会找不到元素
                  //todo: 帮appium打补丁
                  if(kv.getName=="name" && kv.getValue.size>6){
                    log.trace(s"${kv.getName}=${kv.getValue} name size too long")
                  }else {
                    xpath += s"@${kv.getName}=" + "\"" + kv.getValue.replace("\"", "\\\"") + "\""
                  }
                }
              })
              if(xpath.isEmpty){
                path+=node.getNodeName
              }else{
                path+=s"${node.getNodeName}[${xpath.mkString(" and ")}]"
              }
            }else{
              path+=node.getNodeName
            }
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
          nodeMap("xpath") = "//"+path.reverse.takeRight(path.length-2).mkString("/")

          //保持根元素兼容
          if(nodeMap.contains("resource-id")==false && nodeMap.contains("name")==false){
            nodeMap("name")=""
            nodeMap("value")=""
            nodeMap("label")=""
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

          //记录定位方式
          if (nodeMap.contains("bounds")) {
            nodeMap("loc") = nodeMap("xpath")
          }
          if (nodeMap.contains("path")) {
            //nodeMap("loc") = nodeMap("path")
            nodeMap("loc") = nodeMap("xpath")
          }

          nodesMap+=(nodeMap.toMap)
        })
      }
      case _ => {
        log.trace("not node list")
        log.trace(node)
      }
    }
    nodesMap.toList
  }

}
