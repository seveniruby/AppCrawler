import java.io.{BufferedWriter, FileWriter}

import scala.collection.mutable.{Map, ListBuffer}

/**
  * Created by seveniruby on 15/12/18.
  */
case class TreeNode[T](
                        value: T,
                        children: ListBuffer[TreeNode[T]] = ListBuffer[TreeNode[T]]()
                      ) {

  def equals(node: TreeNode[T]): Boolean = {
    node.value == this.value
  }


  def find(tree: TreeNode[T], node: TreeNode[T]): Option[TreeNode[T]] = {
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

  def appendNode(currenTree: TreeNode[T], node: TreeNode[T]): TreeNode[T] = {
    find(currenTree, node) match {
      case Some(v) => {
        return v
      }
      case None => {
        this.children.append(node)
        return node
      }
    }
  }


  def toXml(tree: TreeNode[T]): String = {
    val s=new StringBuffer()
    val before = (tree: TreeNode[T]) => {
      s.append(s"""<node TEXT="${tree.value.toString()}">""")
    }
    val after = (tree: TreeNode[T]) => {
      s.append("</node>")
      s.append("\n")
    }

    s.append("""<map version="1.0.1">""")
    s.append("\n")
    traversal[T](tree, before, after)
    s.append("</map>")
    return s.toString
  }

  def traversal[T](tree: TreeNode[T],
                   before: (TreeNode[T]) => Any = (x: TreeNode[T]) => Unit,
                   after: (TreeNode[T]) => Any = (x: TreeNode[T]) => Unit): Unit = {
    before(tree)
    tree.children.foreach(t => {
      traversal(t, before, after)
    })
    after(tree)
  }

  def generateFreeMind(list: ListBuffer[T], path:String=null): String = {
    val root=TreeNode(list.head)
    var currentNode=root
    list.slice(1, list.size).foreach(e=>{
      currentNode=currentNode.appendNode(root, TreeNode(e))
    })
    val xml=toXml(root)
    if(path!=null){
      val file = new java.io.File(path)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(xml)
      bw.close()
    }
    xml
  }

}


