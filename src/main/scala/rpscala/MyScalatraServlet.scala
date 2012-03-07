package rpscala

import org.scalatra._
import scalate.ScalateSupport

/**
 * #rpscala 72 でのプレゼンテーション表示用に作ったサンプルです
 *
 * @see http://www.scalatra.org/2.0/book/
 */
class MyScalatraServlet extends ScalatraServlet with ScalateSupport {

  // before() {} や after() {} で action の前後をフックするフィルターを追加する
  // () をつけわすれると動かないので注意
  before() {
    // content-type は勝手に設定されないので
    // それぞれのaction の中で適宜指定するか
    // このように一括で指定する
    contentType = "text/html; charset=utf-8"
  }

  // パスを絞ってフィルターする
  // String 型での * はワイルドカード
  before("/admin/*") {
    halt(401, "管理者権限が必要です。")
  }
  get("/admin/.*".r) {
    "管理者なのでアクセスできました。"
  }

  // 最もシンプルな例は get(path), post(path), put(path), delete(path) のように HTTP メソッドとパス指定を記述する
  // （PATCH メソッドもある！)

  // ScalatraKernel に以下のような implicit conversion が存在しているので
  // implicit def string2RouteMatcher(path: String): RouteMatcher = new SinatraRouteMatcher(path, requestPath)
  // このクラスのインスタンス生成時に
  // def get(routeMatchers: RouteMatcher*)(action: => Any) = addRoute(Get, routeMatchers, action)
  // の呼び出しが行われる
  get("/") {

    // ここのブロックは名前渡しになっていて action と呼ばれている
    // action は Any 型を渡せばよいので String や Array[Byte] などを渡す

    // request や response は Servlet API そのまま
    // 使えるものは CoreDsl に定義されているものを参照
    response.addHeader("X-Content-Type-Options", "nosniff")
    request.getCookies.foreach {
      cookie => println(cookie.getName + ":" + cookie.getValue)
    } // CookieSupport を使えば cookies でアクセスできる

    // ScalateSupport にあるメソッドで簡単に Scalate を使える
    // これらのメソッドは String 型を返してそれがそのままボディになる

    val attributes: Map[String, Any] = Map(
      "message" -> "Hello Scalatra!",
      "tosee" -> "ScalatraServlet -> ScalatraKernel, CoreDsl",
      "official" -> "http://www.scalatra.org/",
      "github" -> "https://github.com/scalatra/scalatra")
    val body = templateEngine.layout("/WEB-INF/views/index.ssp", attributes)
    println(body) // HTML をそのまま String 型として返しているだけ

    // 同じことをもう少し簡潔に書けるメソッド
    ssp("index.ssp", attributes.toSeq: _*)
  }

  // HaltException という例外を throw するとそこで処理を中断して
  // 指定された HTTP ステータスでレスポンスする
  get("/halt") {
    val required = params.get("required").getOrElse {
      // HaltException を throw して処理を中断
      halt(400, "required パラメータは必須です。")
    }
    "必須項目が渡されました。 (" + required + ")"
  }

  // リダイレクトはメソッドが用意されている
  get("/google") {
    val q = params.get("q").getOrElse("")
    status(301)
    redirect("http://www.google.com/?q=" + q) // redirect(path) は 内部的に halt() する
  }

  // pass() を使うと一度 route にマッチして action が呼ばれた後で
  // さらにマッチする route を探して処理する
  get("/ScalatraServlet") {
    println("matched GET \"/ScalatraServlet\"")
    "pass() を使ったサンプルです"
  }
  get("/Scalatra.+$".r) {
    println("matched GET \"/Scalatra.+$\".r")
    response.addHeader("Scalatra", "called")
    pass()
  }
  get("/Scala.+$".r) {
    println("matched GET \"/Scala.+$\".r")
    response.addHeader("Scala", "called")
    pass()
  }

  get("/foo") {
    "末尾のスラッシュ有無は区別されます"
  }

  get("/bar/?".r) {
    "正規表現を使えばあってもなくてもどちらでも OK にできます"
  }

  // パラメータの受け取り
  get("/params/get") {
    // apply の場合はもしそのキーで値がなかったら NoSuchElementException が発生する
    val value: String = params("value")
    // get の場合は Option 型で返るので基本的にはこちらを使う方がよい。
    val valueOpt: Option[String] = params.get("value")
    "params.apply(key) または params.get(key) で受け取れます (" + value + ")"
  }
  post("/params/post") {
    val value: String = params.get("value").getOrElse("")
    "params.apply(key) または params.get(key) で受け取れます (" + value + ")"
  }

  // URL 埋め込みパラメータを受け取る
  get("/params/url/:value") {
    // 普通のパラメータと同様に受け取れる
    // 複数埋め込みなどは公式のドキュメントを参照
    val value: String = params.get("value").getOrElse("")
    """URL 埋め込みパラメータは 
      |get("/params/url/:value") {...} の場合、
      |params("value") のようにして受け取ります""".stripMargin + " (" + value + ")"
  }

  get("/multiparams/url/*/*") {
    val splat = multiParams.get("splat").getOrElse(Nil) // ListBuffer(foo, bar)
    "multiParams.get(\"splat\") で取り出します。(" + splat + ")"
  }

  get("/params/url/wildcard/*") {
    val splat = params.get("splat").getOrElse("")
    "URL 中のワイルドカードから取り出す値は「splat」というキーに入っています (" + splat + ")"
  }

  // 正規表現を書ける
  get("/rpscala_\\d+".r) {
    // $ で終わりを指定しないと prefix 指定になるので注意
    "/rpscala_num***"
  }
  get("/rpscala/\\d+/?$".r) {
    "/rpscala/num"
  }

  // デフォルトで Sinatra ライクな route 
  // Rails ライクなものも使える
  // 以下のような implcit conversion を有効にすれば OK
  // implicit override def string2RouteMatcher(path: String) = RailsPathPatternParser(path)

  // 連続パラメータで Boolean をいくつでも受け取れるので条件を追加できる
  get("/hasvalue", request.getParameter("value") != null) {
    "route だけでなく追加の条件にもマッチした場合のみ action が実行されます"
  }

  // 全く同じ route だった場合
  // 下に定義されたものが優先されて
  // 上に定義されているものは無視されるので注意
  // （特にチェック機構などはない）
  get("/dup") {
    // 無視される
  }
  get("/dup") {
    // 無視される
  }
  get("/dup") {
    // GET /dup では必ずここに来る
  }

  // 実装依存の話になるが2.0.3 現在
  // 最後に ScalatraKernel#addRoute された route から順に判定されるので注意
  get("/3番目にマッチする") {}
  get("/次にマッチする") {}
  get("/最初にマッチする") {}

  // マッチする route がなかったらここへ来る
  notFound {
    // マッチする route がなくてwebapp/WEB-INF/views/{route}.ssp
    // のようなテンプレートが置いてあったらそれを表示する
    // 例： GET /hello-scalate
    findTemplate(requestPath) map {
      path =>
        contentType = "text/html"
        layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() // デフォルト実装が呼ばれているがカスタマイズ可能
  }

  override protected def resourceNotFound(): Any = {
    response.getWriter println "これはデフォルトで定義されている resourceNotFound() による出力です<br/><br/>"
    super.resourceNotFound()
  }

}
