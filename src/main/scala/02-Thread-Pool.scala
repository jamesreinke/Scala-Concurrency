import scala.collection._


object ThreadPool extends App{

	private val tasks = mutable.Queue[() => Unit]()
	val worker = new Thread {
		def poll(): Option[() => Unit] = tasks.synchronized {
			if (tasks.nonEmpty) Some(tasks.dequeue()) else None
		}
		override def run() = while (true) poll() match {
			case Some(task) => task()
			case None =>
		}
	}
	worker.setName("James")
	worker.setDaemon(true)
	worker.start()

	def asynchronous(body: => Unit) = tasks.synchronized {
		tasks.enqueue(() => body)
	}
	asynchronous { println("Hello") }
	asynchronous { println(" world!") }
	Thread.sleep(5000)

}