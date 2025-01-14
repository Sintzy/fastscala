package com.fastscala.demo.docs.fastscala

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.SingleCodeExamplePage
import com.fastscala.xml.scala_xml.JS

import java.util.Base64
import scala.xml.NodeSeq


// === code snippet ===
class VisualizeUploadedImageAnonymousPage(
                                           contentType: String,
                                           contents: Array[Byte]
                                         ) extends SingleCodeExamplePage() {

  override def pageTitle: String = "Visualize Uploaded Image Anonymous Page Example"

  override def renderExampleContents()(implicit fsc: FSContext): NodeSeq = {
    import com.fastscala.templates.bootstrap5.classes.BSHelpers._
    JS.rerenderable(rerenderer => implicit fsc => {
      div.border.p_2.rounded.apply {
        h3.apply("Uploaded image:") ++
          <img class="w-100" src={s"data:$contentType;base64, " + Base64.getEncoder.encodeToString(contents)}></img>.mx_auto.my_4.d_block
      }
    }).render()
  }
}
// === code snippet ===