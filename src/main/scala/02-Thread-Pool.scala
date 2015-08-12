import scala.collection._
import Global._

/*
	This type of thread pool keeps our computer humming while it runs through the
		while loop, constantly checking the while condition.
*/
object ThreadPool extends App {

	private val tasks = mutable.Queue[() => Unit]()
	val worker = new Thread {
		def poll: Option[() => Unit] = tasks.synchronized {
			if (tasks.nonEmpty) Some(tasks.dequeue()) else None
		}
		override def run() = while (true) poll match {
			case Some(task) => task
			case None =>
		}
	}
	worker.setName("James")
	worker.setDaemon(true)
	worker.start

	def asynchronous(body: => Unit) = tasks.synchronized {
		tasks.enqueue(() => body)
	}
	asynchronous { println("Hello") }
	asynchronous { println(" world!") }
	Thread.sleep(5000)

}

object Wait extends App {
	val lock = new AnyRef
	var message: Option[String] = None
	val greeter = thread {
		lock.synchronized {
			while (message == None) lock.wait
			println(message.get)
		}
	}
	lock.synchronized {
		message = Some("Hello!")
		lock.notify
	}
	greeter.join
}
/*
	Using the notify and wait API's, we can cause minimal CPU usage while waiting on some piece of data.
*/
object BetterThreadPool extends App {
	private val tasks = mutable.Queue[() => Unit]()
	object Worker extends Thread {
		setDaemon(true)
		def poll = tasks.synchronized {
			while (tasks.isEmpty) tasks.wait
			tasks.dequeue
		}
		override def run = while (true) {
			val task = poll
			task
		}
	}
	def asynchronous(body: => Unit) = tasks.synchronized {
		tasks.enqueue(() => body)
		tasks.notify
	}

	Worker.start
	asynchronous { println("Hello ") }
	asynchronous { println("World!") }
	Thread.sleep(500)
}
/*
	Uses a terminated global for handling these types of temrinations...
*/
object AnotherThreadPool extends App {
	private val tasks = mutable.Queue[() => Unit]()
	object Worker extends Thread {
		var terminated = false
		def poll: Option[() => Unit] = tasks.synchronized {
			while (tasks.isEmpty && !terminated) tasks.wait
			if (!terminated) Some(tasks.dequeue) else None
		}
		def shutdown = tasks.synchronized {
			terminated = true
			tasks.notify
		}
	}
}