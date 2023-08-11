package com.fastscala.templates.utils

import com.fastscala.utils.IdGen

import java.util.UUID

trait ElemWithRandomId extends ElemWithId {

  def randomElemId = IdGen.id

  override val elemId: String = randomElemId
}
