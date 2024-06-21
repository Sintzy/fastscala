package com.fastscala.demo.server

import com.fastscala.server.JettyServerHelper
import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Handler

import java.awt.Desktop
import java.net.URI

object JettyServer extends JettyServerHelper() {

  val config = ConfigFactory.load()

  override def Port: Int = config.getInt("com.fastscala.demo.server.port")

  override def isLocal: Boolean = System.getProperty("user.name") != "fs_demo"

  override def appName: String = "fs_demo"

  override def buildMainHandler(): Handler = new RoutingHandler()

  override def postStart(): Unit = {
    super.postStart()

    if (isLocal && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      val desktop = Desktop.getDesktop()
      desktop.browse(new URI(s"http://localhost:$Port"))
      println(s"Available at: http://localhost:$Port")
    }
  }
}
