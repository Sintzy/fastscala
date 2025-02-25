package com.fastscala.demo.docs.fastscala

import cats.effect.IO
import com.fastscala.core.FSContext
import com.fastscala.demo.docs.SingleCodeExamplePage
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.JS

import scala.concurrent.duration.DurationInt
import scala.xml.NodeSeq
import org.eclipse.jetty.util.VirtualThreads
import java.util.concurrent.Executors

object ServerSidePushPage {
  lazy val singleThreadExecutor = Executors.newSingleThreadExecutor
}

class ServerSidePushPage extends SingleCodeExamplePage() {
  override def pageTitle: String = "Server-Side push using Websockets"

  override def renderExampleContents()(implicit fsc: FSContext): NodeSeq = {
    // === code snippet ===
    import com.fastscala.templates.bootstrap5.classes.BSHelpers._
    val id = IdGen.id
    val N = 30

    Option(VirtualThreads.getDefaultVirtualThreadsExecutor())
      .getOrElse(ServerSidePushPage.singleThreadExecutor)
      .execute { () =>
        def factorial(n: BigInt): IO[BigInt] = for {
          _ <- IO.sleep(100.millis)
          _ <- IO(fsc.sendToPage(JS.prepend2(id, div.apply(s"factorial($n)").text_white_50)))
          rslt <- if (n == 0) IO.pure(BigInt(1)) else factorial(n - 1).map(_ * n)
          _ <- if (n < N) IO(fsc.sendToPage(JS.prepend2(id, div.apply(s"factorial($n) = $rslt").text_white_50))) else IO.unit
        } yield rslt

        import cats.effect.unsafe.implicits.global
        val rslt = factorial(N).unsafeRunSync()
        fsc.sendToPage(JS.prepend2(id, div.apply(s"factorial($N) = $rslt").text_white))
      }

    h2.apply(s"Calculating the factorial($N)") ++
      <div id={id}></div>.m_2.bg_secondary.p_1
        .withStyle("font-family: courier; min-height: 100px;") ++
      JS.inScriptTag(fsc.initWebSocket())
    // === code snippet ===
  }
}
