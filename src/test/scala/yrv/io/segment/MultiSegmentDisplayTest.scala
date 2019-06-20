package yrv.io.segment

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

object MultiSegmentDisplayTests {
  val bits = 9
}

class MultiSegmentDisplayTests[T](c: MultiSegmentDisplay[T]) extends PeekPokeTester(c) {
  private val seg = c

  poke(seg.io.nums(0), 0x3)
  poke(seg.io.nums(1), 0xA)
  poke(seg.io.nums(2), 0x1)
  expect(seg.io.hexes(0), Integer.parseInt("0110000", 2))
  expect(seg.io.hexes(1), Integer.parseInt("0001000", 2))
  expect(seg.io.hexes(2), Integer.parseInt("1111001", 2))
}

class MultiSegmentDisplayTester extends ChiselFlatSpec {
  behavior of "MultiSegmentDisplay"

  backends foreach {backend =>
    it should s"outputs correct digits ($backend)" in {
      Driver(() => new MultiSegmentDisplay(MultiSegmentDisplayTests.bits), backend) {
        c => new MultiSegmentDisplayTests(c)
      } should be (true)
    }
  }
}
