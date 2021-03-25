package com.mirae.next

import org.scalatra._

class ServiceRoute extends ScalatraServlet {

  get("/") {
    views.html.hello()
  }

  error {
//    case e: NoSuchElementException => e.printStackTrace()
//    case e: NumberFormatException => e.printStackTrace()
    case e: Exception => <p>error! in service route</p>
  }
}

class TestRoute extends ScalatraServlet {

  get("/") {
    <p>test Home</p>
  }

  get("/helloworld") {
    <p>helloworld</p>
  }

  error {
    case e: Exception => <p>error! in test route</p>
  }

}