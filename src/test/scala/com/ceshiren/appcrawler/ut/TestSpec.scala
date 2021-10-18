package com.ceshiren.appcrawler.ut

import org.scalatest.FunSpec

/**
  * Created by seveniruby on 16/8/12.
  */
class TestSpec extends FunSpec{
  describe("A Set") {
    describe("when empty") {
      it("should have size 0") {
        assert(Set.empty.size == 0)
      }

      it("should produce NoSuchElementException when head is invoked") {
        assert(1==2)
      }
    }
  }
}
