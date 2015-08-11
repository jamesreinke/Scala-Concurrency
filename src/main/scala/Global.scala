object Global {

	def thread(body: => Unit): Thread = {
		val t = new Thread {
			override def run() = body
		}
		t.start()
		t
	}
	
}