package com.fastscala.templates.bootstrap5.utils

import com.fastscala.code.{FSContext, FSUploadedFile}
import com.fastscala.js.Js
import com.fastscala.utils.IdGen

import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{Elem, NodeSeq}

object FileUpload {

  import com.fastscala.templates.bootstrap5.classes.BSHelpers._

  def apply(
             processUpload: Seq[FSUploadedFile] => Js,
             labelOpt: Option[Elem] = None,
             transformSubmit: Elem => Elem = (_: Elem).btn.apply("Upload").btn.btn_success.mt_2.w_100,
             buttonLbl: Option[String] = None,
             multiple: Boolean = false
           )(implicit fsc: FSContext): NodeSeq = {
    val actionUrl = fsc.fileUploadActionUrl({
      case uploadedFile => processUpload(uploadedFile)
    })
    val targetId = IdGen.id("targetFrame")
    val inputId = IdGen.id("input")
    val buttonId = IdGen.id("btn")
    <iframe id={targetId} name={targetId} src="about:blank" onload="eval(this.contentWindow.document.body.innerText)" style="width:0;height:0;border:0px solid #fff;"><html><body></body></html></iframe>
    <form target={targetId} action={actionUrl} method="post" encoding="multipart/form-data" enctype="multipart/form-data" >
      {
      labelOpt.map(label => label.withFor(inputId)).getOrElse(NodeSeq.Empty)
      }
      <input class="form-control" name="file" type="file" multiple={Some("true").filter(_ => multiple).getOrElse(null)} id={inputId} onchange={Js.show(buttonId).cmd} />
      {
      transformSubmit(button.withId(buttonId).withStyle("display:none").pipe(btn => buttonLbl.map(lbl => btn.apply(lbl)).getOrElse(btn)).withTypeSubmit())
      }
    </form>
  }

  def withZipSupport(
                      callback: List[(String, Array[Byte])] => Js,
                      labelOpt: Option[Elem] = None,
                      transformSubmit: Elem => Elem = (_: Elem).btn.apply("Upload").btn.btn_success.mt_2.w_100,
                      buttonLbl: Option[String] = None,
                      multiple: Boolean = false
                    )(implicit fsc: FSContext): NodeSeq = apply(uploadedFiles =>

    callback(uploadedFiles.flatMap(uploadedFile => {
      if (uploadedFile.name.trim.toLowerCase.endsWith(".zip")) {
        val zipFile = new ZipInputStream(new ByteArrayInputStream(uploadedFile.content))

        Iterator.continually(zipFile.getNextEntry).takeWhile(_ != null).map(entry => {
          (entry.getName, Iterator.continually(zipFile.read()).takeWhile(_ >= 0).map(_.toByte).toArray[Byte])
        }).toList
      } else {
        List((uploadedFile.name, uploadedFile.content))
      }
    }).toList)
    , labelOpt = labelOpt
    , transformSubmit = transformSubmit
    , buttonLbl = buttonLbl
    , multiple = multiple
  )
}
