import de.rolandgreim.krobotremoteserver.RobotKeyword
import de.rolandgreim.krobotremoteserver.RobotLibrary
import de.rolandgreim.krobotremoteserver.robotFrameworkServer
import io.ktor.server.engine.*
import io.ktor.server.netty.*

@RobotLibrary(
    introduction = """
        This is a test library. It has two methods.
    """,
    importing = """
        Never import this! It's just for testing :D
    """
)
class MyLibrary {
    @RobotKeyword(tags = ["math"], documentation = "2 * a")
    fun `multiply with two`(a: Int) = 2 * a

    @RobotKeyword(tags = ["math"], documentation = "a + b")
    fun addition(a: Int, b: Int) = a + b
}

fun main() {
    val myLibrary = MyLibrary()

    embeddedServer(Netty, port = 8272) {
        robotFrameworkServer(myLibrary)
    }.start(wait = true)
}