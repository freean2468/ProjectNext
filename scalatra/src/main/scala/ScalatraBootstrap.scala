import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import com.mirae.next.{ServiceRoute, TestRoute};

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new ServiceRoute, "/")
    context.mount(new TestRoute, "/test")
  }
}
