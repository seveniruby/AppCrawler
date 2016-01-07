/**
 * Created by seveniruby on 15/11/1.
 */


// usagex`x
object Test extends App {
  def faulty: Int = throw new Exception
  println("hello")
  Thread.sleep(3000)
  val x = 1
  val y = x + faulty
  x + y

}