package com.fastscala.templates.bootstrap5.components

import com.fastscala.code.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.utils.BSBtn

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

object BSBtnToogle {

  implicit class RichBSBtnToogler(btn: BSBtn) {

    def toggler(
                 get: () => Boolean,
                 set: Boolean => Js,
                 falseLbl: String,
                 trueLbl: String,
                 falseTransform: BSBtn => BSBtn = identity[BSBtn],
                 trueTransform: BSBtn => BSBtn = identity[BSBtn]
               )(implicit fsc: FSContext): Elem = {
      var current = get()
      Js.rerenderable(rerenderer => implicit fsc => {
        btn.lbl(if (current) trueLbl else falseLbl)
          .ajax(implicit fsc => {
            current = !current
            set(current) & rerenderer.rerender()
          }).pipe(if (current) trueTransform else falseTransform).btn
      }).render()
    }
  }
}
