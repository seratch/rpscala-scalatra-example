# Scalatra Example App

## rpscala 

http://scala-users.org/shibuya/

http://partake.in/events/4488e148-a614-49fd-bced-20fd896f4461

## Scalatra

http://www.scalatra.org/

https://github.com/scalatra/scalatra

## Getting Started

もし sbt 設定されていない場合はルートディレクトリに置いてあるスクリプトを使ってください。

```sh
git clone git://github.com/seratch/rpscala-scalatra-example.git
cd ./rpscala-scalatra-example
./sbt
```

### Jetty を起動

sbt シェルが起動したら web-plugin で Jetty を起動します。

```
container:start
```

ブラウザで http://localhost:8080/ にアクセスしてください。


### 変更したら Jetty 再起動

```
~;container:stop;container:start
```


