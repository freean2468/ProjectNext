package com.mirae.next

import com.mirae.next.database.QuerySupport
import com.mirae.next.database.Tables.{Daily, Ticker}
import org.json4s
import org.json4s.JsonAST.JObject
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.JdbcBackend.Database

import scala.xml.{Elem, Node}
import java.sql.SQLIntegrityConstraintViolationException
import scala.concurrent.{ExecutionContext, Future}

trait RootRoute extends ScalatraBase with JacksonJsonSupport with FutureSupport with QuerySupport with FileUploadSupport {
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3*1024*1024)))
  /** Sets up automatic case class to JSON output serialization, required by the JValueResult trait. */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database

  private def displayPage(title:String, content:Seq[Node]): Elem = Template.page(title, content, url(_))

  /** get
   *
   */
  get("/") {
    displayPage("project next 서버에 오신 것을 환영합니다.",
      <p>다음과 같은 경로에 서비스가 제공됩니다.</p>
        <p>get : </p>
        <p>/dailies : daily 정보를 서버로부터 불러옵니다.</p>
        <p>/tickers : ticker 정보를 서버로부터 불러옵니다. </p>
        <p>/daily/1?ticker='' : ticker의 daily 정보를 불러옵니다.</p>
        <p>/ticker/1?ticker='' : ticker를 불러옵니다.</p>
        <br></br>
        <p>post : </p>
        <p>/daily/1?ticker=''&amp;date=''&amp;open=''&amp;high=''&amp;low=''
          &amp;close=''&amp;volume='' : daily 정보를 db에 저장합니다.</p>
        <p>/ticker/1?ticker='' : ticker 정보를 db에 저장합니다.</p>
    )
  }

  get("/dailies") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        selectDailyAll()
      }
    }
  }

  get("/tickers") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        selectTickerAll()
      }
    }
  }

  get("/ticker/1?") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        selectTicker(params.getOrElse("ticker", halt(400)))
      }
    }
  }

  get("/daily/1?") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        val logger = LoggerFactory.getLogger(getClass)
        logger.info(params.get("ticker").get)
        selectDaily(params.getOrElse("ticker", halt(400)))
      }
    }
  }

  /** post
   *
   */
  post("/ticker/:none") {
    new AsyncResult { override val is =
      Future {
        contentType = ""
        val ticker = params.getOrElse("ticker", "")
        insertTicker(Ticker(ticker))
      }
    }
  }

  post("/dailies") {
    val logger = LoggerFactory.getLogger(getClass)

    new AsyncResult { override val is =
      Future {
//        val list = for {
//          jvalue <- parse(request.body)
//        } jvalue.ex
//
//        for (obj <- list) {
//
//        }

//        val ticker = params.getOrElse("ticker", "")
//        val date = params.getOrElse("date", "")
//        val open = params.getOrElse("open", "").toDouble
//        val high = params.getOrElse("high", "").toDouble
//        val low = params.getOrElse("low", "").toDouble
//        val close = params.getOrElse("close", "").toDouble
//        val volume = params.getOrElse("volume", "").toLong
//
//        insertDaily(Daily(ticker, date, open, high, low, close, volume))
      }
    }
  }

  post("/daily/:none") {
    val logger = LoggerFactory.getLogger(getClass)

    new AsyncResult { override val is =
      Future {
        contentType = ""
        val ticker = params.getOrElse("ticker", "")
        val date = params.getOrElse("date", "")
        val open = params.getOrElse("open", "").toDouble
        val high = params.getOrElse("high", "").toDouble
        val low = params.getOrElse("low", "").toDouble
        val close = params.getOrElse("close", "").toDouble
        val volume = params.getOrElse("volume", "").toLong

        insertDaily(Daily(ticker, date, open, high, low, close, volume))
      }
    }
  }

  error {
//    case e: NoSuchElementException => e.printStackTrace()
//    case e: NumberFormatException => e.printStackTrace()
    case e: SQLIntegrityConstraintViolationException => {
      e.printStackTrace()
      <p>constraint violation!</p>
    }
    case e: Exception => {
      e.printStackTrace()
      <p>error! in service route</p>
    }
  }
}

/** 서비스를 제공하는 routing 클래스
 *
 * @param db config 정보
 */
class RootRouteServlet (val db: Database) extends ScalatraServlet with RootRoute {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}

object Template {

  def page(
            title:String,
            content:Seq[Node],
            url: String => String = identity,
            head: Seq[Node] = Nil,
            scripts: Seq[String] = Seq.empty,
            defaultScripts: Seq[String] = Seq("/assets/js/jquery.min.js", "/assets/js/bootstrap.min.js")
          ): Elem = {
    <html lang="en">
      <head>
        <title>{ title }</title>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="" />
        <meta name="author" content="" />

        <!-- Le styles -->
        <link href="/assets/css/bootstrap.css" rel="stylesheet" />
        <link href="/assets/css/bootstrap-responsive.css" rel="stylesheet" />
        <link href="/assets/css/syntax.css" rel="stylesheet" />
        <link href="/assets/css/scalatra.css" rel="stylesheet" />

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
        {head}
      </head>

      <body>
        <div class="navbar navbar-inverse navbar-fixed-top">
          <div class="navbar-inner">
            <div class="container">
              <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </a>
              <a class="brand" href="/">Scalatra Examples</a>
              <div class="nav-collapse collapse">

              </div><!--/.nav-collapse -->
            </div>
          </div>
        </div>

        <div class="container">
          <div class="content">
            <div class="page-header">
              <h1>{ title }</h1>
            </div>
            <div class="row">
              <div class="span9">
                {content}
              </div>
              <hr/>
            </div>
          </div> <!-- /content -->
        </div> <!-- /container -->
        <footer class="vcard" role="contentinfo">

        </footer>

        <!-- Le javascript
          ================================================== -->
        <!-- Placed at the end of the document so the pages load faster -->
        { (defaultScripts ++ scripts) map { pth =>
        <script type="text/javascript" src={pth}></script>
      } }

      </body>

    </html>
  }
}