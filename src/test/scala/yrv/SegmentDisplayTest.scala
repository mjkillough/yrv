package yrv

import java.io.File

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class SegmentDisplayTests(c: SegmentDisplay) extends PeekPokeTester(c) {
  private val seg = c

  def assertDigit(in: Int, out: Int) = {
    poke(seg.io.num, in)
    expect(seg.io.hex, out)
    step(1)
  }

  assertDigit(0x0, Integer.parseInt("1000000", 2))
  assertDigit(0x1, Integer.parseInt("1111001", 2))
  assertDigit(0x2, Integer.parseInt("0100100", 2))
  assertDigit(0x3, Integer.parseInt("0110000", 2))
  assertDigit(0x4, Integer.parseInt("0011001", 2))
  assertDigit(0x5, Integer.parseInt("0010010", 2))
  assertDigit(0x6, Integer.parseInt("0000010", 2))
  assertDigit(0x7, Integer.parseInt("1111000", 2))
  assertDigit(0x8, Integer.parseInt("0000000", 2))
  assertDigit(0x9, Integer.parseInt("0010000", 2))
  assertDigit(0xA, Integer.parseInt("0001000", 2))
  assertDigit(0xB, Integer.parseInt("0000011", 2))
  assertDigit(0xC, Integer.parseInt("1000110", 2))
  assertDigit(0xD, Integer.parseInt("0100001", 2))
  assertDigit(0xE, Integer.parseInt("0000110", 2))
  assertDigit(0xF, Integer.parseInt("0001110", 2))
}

class SegmentDisplayTester extends ChiselFlatSpec {
  behavior of "SegmentDisplay"

  backends foreach {backend =>
    it should s"outputs correct digit ($backend)" in {
      Driver(() => new SegmentDisplay, backend) {
        c => new SegmentDisplayTests(c)
      } should be (true)
    }
  }
}
