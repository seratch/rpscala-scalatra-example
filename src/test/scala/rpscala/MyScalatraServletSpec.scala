package rpscala

import org.scalatra.test.specs2._

// For more on Specs2, 
// see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html 

class MyScalatraServletSpec extends ScalatraSpec {
  def is =

    "GET / on MyScalatraServlet" ^
      "should return status 200" ! root200 ^
      "should return X-Content-Type-Options header" ! rootXContentTypeOptions ^
      "should return the expected body" ! rootBody ^
      end

  addServlet(classOf[MyScalatraServlet], "/*")

  // ScalatraTests に定義された以下を使う
  // def response = _response value
  // def body = response.body
  // def header = response.header
  // def status = response.status

  def root200 = get("/") {
    status must_== 200
  }

  def rootXContentTypeOptions = get("/") {
    response.getHeader("X-Content-Type-Options") must_== ("nosniff")
  }

  def rootBody = get("/") {
    body must contain("""Hello Scalatra!""")
  }


}
