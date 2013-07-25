package remind

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.future

object Remind extends App {
  case class Reminder(delay: Duration, msg: String) extends Delayed {
    private val expired = delay.toMillis + System.currentTimeMillis()

    override def getDelay(unit: TimeUnit): Long = {
      (expired - System.currentTimeMillis()).millis.toUnit(unit).toLong
    }

    override def compareTo(otherDelayed: Delayed) = otherDelayed match {
      case other: Reminder => delay.compareTo(other.delay)
    }
  }

  val q = new DelayQueue[Reminder]()

  future {
    while (true) {
      println(s"wait for q")
      val reminder = q.take()
      println(s"Remind.drain: ${reminder.msg}")
    }
  }

  val in = new BufferedReader(new InputStreamReader(System.in));
  while (true) {
    val r = in.readLine()
    println(s"add $r")
    q.add(new Reminder(10 seconds, r))
  }
}