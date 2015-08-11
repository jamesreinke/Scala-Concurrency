import Global._
/*
	Book believes this should fail, when it in fact doesn't...
		There must have been some change to the JVM specifications.
*/
object JVM extends App {

	for (i <- 0 until 100000) {
		var a = false
		var b = false
		var x = -1
		var y = -1
		val t1 = thread {
			a = true
			y = if (b) 0 else 1
		}
		val t2 = thread {
			b = true
			x = if (a) 0 else 1
		}
		t1.join()
		t2.join()
		assert(!(x == 1 && y == 1), s"x = $x, y = $y")
	}

}
