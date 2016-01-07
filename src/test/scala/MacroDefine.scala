/**
 * Created by seveniruby on 15/11/1.
 */
class MacroDefine {

}


import scala.reflect.macros.whitebox
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox
import scala.reflect.runtime.universe._
import scala.language.experimental.macros

// macro that prints expression code before executing it
object debug {
  def apply[T](x: =>T): T = macro impl
  def impl(c: whitebox.Context)(x: c.Tree) = {
    import c.universe._
    val q"..$stats" = x
    println(stats)
    val loggedStats = stats.flatMap { stat =>
      val msg = "executing " + showCode(stat)
      val raw = "executing " + showRaw(stat)
      val code = showCode(stat)
/*      reify{
        println(msg)
        println(code)
        println(raw)
        10 to 15 foreach(i=>{
          println(i)
        })
      }*/

      List(q"println($msg)",
        q"println($raw)",
        q"println($code)",
        q"1 to 3 foreach(i=>{println(i); $code;..$code;println(44)})", stat)
    }
    q"..$loggedStats"
  }
}
