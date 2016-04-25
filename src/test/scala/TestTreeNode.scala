import com.xueqiu.qa.appcrawler.TreeNode
import org.scalatest.FunSuite

import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/2/10.
  */
class TestTreeNode extends FunSuite{
  test("generate tree"){
    val root=TreeNode("root")
    root.appendNode(root, TreeNode("1")).appendNode(root, TreeNode("11")).appendNode(root, TreeNode("111"))
    root.appendNode(root, TreeNode("2")).appendNode(root, TreeNode("21"))
    root.appendNode(root, TreeNode("3"))
    root.toXml(root)

  }

  test("generate tree by list"){
    val list=ListBuffer(1, 2, 3, 4, 1, 5, 6, 5, 7)
    TreeNode(0).generateFreeMind(list, "1.mm")
  }

  test("append node single"){
    val root=TreeNode(0)
    var current1=root.appendNode(root, TreeNode(1))
    println(current1)
    var current2=current1.appendNode(root, TreeNode(2))
    println(root)
    println(current1)
    println(current2)
  }

}
