import java.io.{StringWriter, ByteArrayInputStream}
import java.nio.charset.StandardCharsets
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPathConstants, XPathFactory, XPath}

import org.apache.xml.serialize.{XMLSerializer, OutputFormat}
import org.w3c.dom.{Attr, NodeList, Document}

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
          println(nodeList.item(i))

          val node=nodeList.item(i)
          println(node.getNodeValue)
          nodeMap(node.getNodeName)=node.getNodeValue
          val nodeAttributes = node.getAttributes
          println(nodeAttributes)
          if(nodeAttributes!=null) {
            nodeMap("tag") = nodeList.item(i).getNodeName
            0 until nodeAttributes.getLength foreach (a => {
              val attr = nodeAttributes.item(a).asInstanceOf[Attr]
              nodeMap(attr.getName) = attr.getValue
            })
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
