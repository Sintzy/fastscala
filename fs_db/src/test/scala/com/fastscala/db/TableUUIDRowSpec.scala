package com.fastscala.db

import org.scalatest.flatspec.AnyFlatSpec
import scalikejdbc._

class TestEntity4(
                   var myInt: Int = 123,
                   var myLong: Long = 321,
                   var myDouble: Double = 1.234,
                   var myFloat: Float = 4.321f,
                   var myShort: Short = 777,
                   var myString: String = "hello",
                   var myBoolean: Boolean = true,
                   var myChar: Char = 'X',
                 ) extends RowWithUUID[TestEntity4] {
  override def table: TableWithUUID[TestEntity4] = TestEntity4
}

object TestEntity4 extends TableWithUUID[TestEntity4] {
  override def createSampleRow(): TestEntity4 = new TestEntity4()
}

class TableUUIDRowSpec extends AnyFlatSpec with DBTests {

  "Create table" should "succeed" in {
    DB.localTx({ implicit session =>
      TestEntity4.__createTableSQL.execute()()
    })
  }
  "Save row" should "succeed" in {
    DB.localTx({ implicit session =>
      val saved = new TestEntity4().save()
      assert(saved.uuid.isDefined)
    })
  }
  "Read row" should "succeed" in {
    DB.localTx({ implicit session =>
      val example = new TestEntity4()
      val single = TestEntity4.listAll().head

      assert(example.myInt == single.myInt)
      assert(example.myLong == single.myLong)
      assert(example.myDouble == single.myDouble)
      assert(example.myFloat == single.myFloat)
      assert(example.myShort == single.myShort)
      assert(example.myString == single.myString)
      assert(example.myBoolean == single.myBoolean)
      assert(example.myChar == single.myChar)
    })
  }
  "Update row" should "succeed" in {
    DB.localTx({ implicit session =>
      val single = TestEntity4.listAll().head

      single.myInt += 1
      single.myLong += 1
      single.myDouble += 1
      single.myFloat += 1
      single.myShort = 778
      single.myString += "!"
      single.myBoolean = false
      single.myChar = '_'

      single.update()

      assert(TestEntity4.listAll().size == 1)

      val inDB = TestEntity4.listAll().head

      assert(single.uuid.isDefined)

      assert(single.uuid == inDB.uuid)

      assert(inDB.myInt == single.myInt)
      assert(inDB.myLong == single.myLong)
      assert(inDB.myDouble == single.myDouble)
      assert(inDB.myFloat == single.myFloat)
      assert(inDB.myString == single.myString)
      assert(inDB.myBoolean == single.myBoolean)
      assert(inDB.myShort == single.myShort)
      assert(inDB.myChar == single.myChar)
    })
  }
  "Select by UUID" should "succeed" in {
    DB.localTx({ implicit session =>
      new TestEntity4().save()
      new TestEntity4().save()
      val uuids = TestEntity4.listAll().map(_.uuid.get)

      assert(TestEntity4.forUUID(uuids: _*).size == 3)
    })
  }
  "Delete table" should "succeed" in {
    DB.localTx({ implicit session =>
      TestEntity4.__dropTableSQL.execute()()
    })
  }
}
