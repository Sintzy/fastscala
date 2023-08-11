package com.fastscala.templates.bootstrap5.tables

import com.fastscala.code.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.classes.BSHelpers.s
import com.fastscala.templates.bootstrap5.modals.BSModal5
import com.fastscala.templates.bootstrap5.utils.{BSBtn, ImmediateInputFields}
import com.fastscala.utils.Lazy
import com.fastscala.utils.NodeSeqUtils.MkNSFromNodeSeq

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

trait Table5SelectableRows extends Table5Base with Table5ColsLabeled {

  import com.fastscala.templates.bootstrap5.classes.BSHelpers._

  lazy val allSelectedRowsEvenIfNotVisible = collection.mutable.Set[R]()

  def selectedVisibleRows: Set[R] = rows(rowsHints()).toSet intersect allSelectedRowsEvenIfNotVisible.toSet

  override def transformTRTDElem(elem: Elem)(implicit tableBodyRerenderer: TableBodyRerenderer, trRerenderer: TRRerenderer, col: C, value: R, rowIdx: TableRowIdx, columns: Seq[(String, C)], rows: Seq[(String, R)], fsc: FSContext): Elem = {
    super.transformTRTDElem(elem)
      .pipe(elem => if (allSelectedRowsEvenIfNotVisible.contains(value)) elem.bg_primary_subtle else elem)
      .pipe(elem => col match {
        case ColSelectRow => elem.align_middle.text_center
        case _ => elem
      })
  }

  def onSelectedRowsChange()(implicit fsc: FSContext): Js = Js.void

  def selectAllVisibleRowsBtn: BSBtn = BSBtn.BtnOutlinePrimary.lbl(s"Select All").ajax(implicit fsc => {
    allSelectedRowsEvenIfNotVisible.clear()
    allSelectedRowsEvenIfNotVisible ++= rows(rowsHints())
    onSelectedRowsChange() &
      rerenderTableAround()
  })

  def clearRowSelectionBtn: BSBtn = BSBtn.BtnOutlinePrimary.lbl(s"Clear Selection").ajax(implicit fsc => {
    allSelectedRowsEvenIfNotVisible.clear()
    onSelectedRowsChange() &
      rerenderTableAround()
  })

  val ColSelectRow = new Table5StandardColumn[R] {

    override def label: String = ""

    override def renderTH()(implicit tableHeadRerenderer: TableHeadRerenderer, trRerenderer: TRRerenderer, thRerenderer: THRerenderer, colIdx: TableColIdx, pageRows: Seq[(String, R)], fsc: FSContext): Elem = <th></th>

    override def renderTD()(implicit tableBodyRerenderer: TableBodyRerenderer, trRerenderer: TRRerenderer, tdRerenderer: TDRerenderer, value: R, rowIdx: TableRowIdx, colIdx: TableColIdx, rows: Seq[(String, R)], fsc: FSContext): Elem = {
      val contents = ImmediateInputFields.checkbox(() => allSelectedRowsEvenIfNotVisible.contains(value), selected => {
        if (selected) allSelectedRowsEvenIfNotVisible += value
        else allSelectedRowsEvenIfNotVisible -= value
        onSelectedRowsChange() &
          trRerenderer.rerenderer.rerender()
      }, "").m_0
      <td>{contents}</td>
    }
  }
}
