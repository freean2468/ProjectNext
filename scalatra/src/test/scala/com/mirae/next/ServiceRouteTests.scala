package com.mirae.next

import org.scalatra.test.scalatest._

class ServiceRouteTests extends ScalatraFunSuite {

  addServlet(classOf[ServiceRoute], "/*")

  test("GET / on ServiceRoute should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
