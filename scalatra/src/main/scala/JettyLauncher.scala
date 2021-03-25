import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/** AWS 환경에서 웹서버 운용을 위해 필요한 Object. Jetty 웹서를 실행시킨다. ScalatraBootstrap을 실행시킨다.
 * 이떄 build.sbt 안에서 jetty dependency는 compile 옵션을 줘야한다.
 *
 */
object JettyLauncher {
  def main(args: Array[String]): Unit = {
    val port = if (System.getProperty("http.port") != null) System.getProperty("http.port").toInt else 8080
    val server = new Server(port)
    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase("src/main/webapp")
    context.setEventListeners(Array(new ScalatraListener))

    server.setHandler(context)

    server.start
    server.join
  }
}
