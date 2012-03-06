package rpscala

import org.scalatra._
import scalate.ScalateSupport

/**
 * #rpscala 72 でのプレゼンテーション表示用に作ったサンプルです
 */
class MyScalatraServlet extends ScalatraServlet with ScalateSupport {

  // content-type は勝手に設定されないので
  // それぞれのaction の中で適宜指定するか
  // このように一括で指定する
  before() {
    contentType = "text/html; charset=utf-8"
  }

  // 最もシンプルな例は get(path), post(path), put(path), delete(path) のように
  // HTTP メソッドとパス指定を記述する
  get("/") {

    // ここのブロックは名前渡しになっていて action と呼ばれている
    // action は Any 型を渡せばよいので String や Array[Byte] などを渡す

    // ScalateSupport にあるメソッドで簡単に Scalate を使える
    // これらのメソッドは String 型を返してそれがそのままボディになる
    // layoutTemplate("index.ssp", ("message" -> "Hello Scalatra!"))
    ssp("index.ssp", 
      ("message" -> "Hello Scalatra!"),
      ("tosee" -> "ScalatraServlet -> ScalatraKernel, CoreDsl"),
      ("official" -> "http://www.scalatra.org/"),
      ("github" -> "https://github.com/scalatra/scalatra")
    )
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
  get("/3番目にマッチする") { }
  get("/次にマッチする") { }
  get("/最初にマッチする") { }

  // マッチする route がなかったらここへ来る
  notFound {
    // マッチする route がなくてwebapp/WEB-INF/views/{route}.ssp
    // のようなテンプレートが置いてあったらそれを表示する
    // 例： GET /hello-scalate
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() // デフォルト実装が呼ばれているがカスタマイズ可能
  }

  override protected def resourceNotFound(): Any = {
    response.getWriter println "これはデフォルトで定義されている resourceNotFound() による出力です<br/><br/>" 
    super.resourceNotFound()
  }

}
