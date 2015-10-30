import org.scalatest.FunSuite
import scala.reflect.runtime.universe._

import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

/**
 * Created by seveniruby on 15/10/17.
 */



object traverser extends Traverser {
  var applies = List[Apply]()
  val tb=currentMirror.mkToolBox()
  override def traverse(tree: Tree): Unit = tree match {
    case app @ Apply(fun, args) =>
      println("find function")
      println(fun)
      applies = app :: applies
      fun match {
        case "click" =>{

        }
      }
      super.traverse(fun)
      super.traverseTrees(args)
    case Block(statements,_) =>
      println("find block")
      statements.foreach(x=>{
        println(x)
        tb.eval(x)
        println("next")
      })
    case _ => {
      println("find line")
      println(_:Any)
      super.traverse(tree)
    }
  }
}



class ast extends FunSuite{
  test("ast tree travel"){
    val tree=reify{
      val a=1
      val b=2
      println(a+b)
      println("demo")
      1 to 5 foreach(i=>{
        println(i)
      })
    }
    traverser.traverse(tree.tree)

  }

}
