import Global._
import UniqueId.getUniqueId
object BankAcconts extends App {

	import scala.collection._
	val transfers = mutable.ArrayBuffer[String]()
	def logTransfer(name: String, n: Int) = transfers.synchronized {
		transfers += s"transfer to account '$name' = $n"
	}

	class Account(val name: String, var money: Int) {
		val uid = getUniqueId()
	}
	def add(account: Account, n: Int) = account.synchronized {
		account.money += n
		if (n > 10) logTransfer(account.name, n) 
	}
	def send(a1: Account, a2: Account, n: Int) = a.synchronized {
		def adjust() {
			a1.money -= n
			a2.money += n
		}
		if (a1.uid < a2.uid)
			a1.synchronized {
				a2.synchronized {
					adjust()
				}
			}
		else a2.synchronized {
			a1.synchronized {
				adjust()
			}
		}
	}

	val a = new Account("James", 1000)
	val b = new Account("Bryan", 2000)
	val t1 = thread { for (i <- 0 until 100) send(a, b, 1) }
	val t2 = thread { for (i <- 0 until 100) send(b, a, 1) }
	t1.join(); t2.join()
	println(s"a = ${a.money}, b = ${b.money}")

}