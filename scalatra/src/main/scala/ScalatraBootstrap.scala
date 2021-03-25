import org.scalatra.LifeCycle

import javax.servlet.ServletContext
import com.mirae.next.{ServiceRouteServlet}
import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend.Database;

/** Scala 언어 기반 Web Framework Scalatra를 이용한 웹서버
 *
 * @author 송훈일(sensebe)
 * @since 2021.03.24 ~
 * @version 0.1
 */

/** Scala 언어 기반 Web Framework Scalatra의 main 함수격인 클래스.
 *
 */
class ScalatraBootstrap extends LifeCycle{

  /** Scalatra 앱 실행 시 실행되는 초기화 함수. servlet에 routing class를 mount
   *
   * @param context 현재 servlet context
   */
  override def init(context: ServletContext) {
    /** System.getenv를 통해 현재 서버가 local인지 ec2인지 구분한다.
     *  db 정보는 application.conf 파일에 따로 보관하며 이는 github을 통해 노출하지 않는다.
     */
    val isEb = System.getenv("IS_EB")
    val rdsMysql = ConfigFactory.parseResources("application.conf")

    val serviceDb = isEb match {
      case "1" => {
        Database.forURL(
          rdsMysql.getString("RDSMySQL.url"),
          rdsMysql.getString("RDSMySQL.user"),
          rdsMysql.getString("RDSMySQL.password")
        )
      }
      case _ => {
        Database.forConfig("localMySQL")
      }
    }

    context.mount(new ServiceRouteServlet(serviceDb), "/service")
  }
}