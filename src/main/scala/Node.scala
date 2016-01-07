import scala.collection.mutable.{Map, ListBuffer}

/**
  * Created by seveniruby on 15/12/18.
  */
  case class Node[T](value: T, children: ListBuffer[Node[T]] = ListBuffer[Node[T]]()) {
    type AM = Map[String, String]
    var nId = 0

    def getNodeId(): String = {
      nId += 1
      return nId.toString
    }

    def getArrowId(): String = {
      nId += 1
      return nId.toString
    }

    def equals(node: Node[Map[String, String]]): Boolean = {
      List("url", "id", "name").foreach(attr => {
        if (node.value(attr) != value.asInstanceOf[Map[String, String]](attr)) {
          return false
        }
      })
      return true
    }

    def appendNodes(currenTree: Node[AM], node: Node[AM], lastAddedNodes: ListBuffer[Node[AM]]): ListBuffer[Node[AM]] = {
      var newTree = currenTree
      var addedNodes = ListBuffer[Node[AM]]()

      //add url node
      if (node.value("url") != null) {
        val newNode = Node(Map("url" -> node.value("url"),
          "id" -> null,
          "name" -> null))
        newTree = appendNode(newTree, newNode)
        addedNodes += newTree
      }
      //add id node
      if (node.value("id") != null) {
        val newNode = Node(Map("url" -> node.value("url"),
          "id" -> node.value("id"),
          "name" -> null))
        newTree = appendNode(newTree, newNode)
        addedNodes += newTree
      }
      //add name node
      if (node.value("name") != null) {
        val newNode = Node(Map("url" -> node.value("url"),
          "id" -> node.value("id"),
          "name" -> node.value("name")))
        newTree = appendNode(newTree, newNode)
        addedNodes += newTree
      }
      //add targetlink to just append node
      if (lastAddedNodes.length > 0 && addedNodes.length > 0 && lastAddedNodes.last.value("url") != addedNodes.head.value("url")) {
        var arrowId = getArrowId()
        //add attrs of linktarget to new node
        addedNodes.head.value += ("type" -> "linktarget")
        addedNodes.head.value += ("destination" -> addedNodes.head.getNodeId())
        addedNodes.head.value += ("source" -> lastAddedNodes.last.getNodeId())
        addedNodes.head.value += ("aid" -> s"Arrow_ID_${arrowId}")

        //add attrs of arrowlink to the last node
        lastAddedNodes.last.value += ("type" -> "arrowlink")
        lastAddedNodes.last.value += ("destination" -> addedNodes.head.getNodeId())
        lastAddedNodes.last.value += ("aid" -> s"Arrow_ID_${arrowId}")
      }

      return addedNodes
    }

    def appendNode(currenTree: Node[AM], node: Node[AM]): Node[AM] = {
      find(currenTree, node) match {
        case Some(v) => {
          return v
        }
        case None => {
          currenTree.children.append(node)
          return node
        }
      }
    }


    def toXml(tree: Node[AM]): Unit = {
      val before = (tree: Node[AM]) => {
        var output = ""
        if (tree.value("name") != null) {
          output = tree.value("name")
        } else if (tree.value("id") != null) {
          output = tree.value("id")
        } else if (tree.value("url") != null) {
          output = tree.value("url")
        }
        println( s"""<node ID="ID_${tree.nId}" TEXT="${output}">""")

        //add linktarget and arrowlink if needed
        if (tree.value.contains("type")) {
          tree.value("type") match {
            case "linktarget" => {
              println( s"""<linktarget COLOR="#b0b0b0" DESTINATION="ID_${tree.value("destination")}" ENDARROW="Default" ENDINCLINATION="24;0;" ID="${tree.value("aid")}" SOURCE="${tree.value("source")}" STARTARROW="None" STARTINCLINATION="24;0;"/>""")
            }
            case "arrowlink" => {
              println( s"""<arrowlink DESTINATION="ID_${tree.value("destination")}" ENDARROW="Default" ENDINCLINATION="24;0;" ID="${tree.value("aid")}" STARTARROW="None" STARTINCLINATION="24;0;"/>""")
            }
          }
        }
      }
      val after = (tree: Node[AM]) => {
        println("</node>")
      }
      traversal[AM](tree, before, after)
    }

    def traversal[T](tree: Node[T],
                     before: (Node[T]) => Unit = (x: Node[T]) => Unit,
                     after: (Node[T]) => Unit = (x: Node[T]) => Unit): Unit = {
      before(tree)
      tree.children.foreach(t => {
        traversal(t, before, after)
      })
      after(tree)
    }

    def find(tree: Node[AM], node: Node[AM]): Option[Node[AM]] = {
      if (tree.equals(node)) {
        return Some(tree)
      }
      tree.children.map(t => {
        find(t, node) match {
          case Some(v) => return Some(v)
          case None => {}
        }
      })
      return None
    }

    def generateFreeMind(list: ListBuffer[Element]): Unit = {
      val freemind = Node(Map("url" -> "Start", "id" -> "Start", "name" -> null))
      // 保留上一个node用来加linktarget箭头
      var lastAddedNodes = ListBuffer[Node[AM]]()
      list.foreach(l => {
        var fixedUrl = l.url
        // 去掉url的前缀: android/gz  com.xueqiu.android/gz
        if (l.url.split("/").length > 1) {
          fixedUrl = l.url.split("/")(1)
        }
        var nameNode = Node(Map("url" -> fixedUrl,
          "id" -> l.id,
          "name" -> l.name))
        lastAddedNodes = appendNodes(freemind, nameNode, lastAddedNodes)
      })

      println(freemind)

      println( """<map version="1.0.1">""")
      toXml(freemind)
      println("</map>")

    }

  }


