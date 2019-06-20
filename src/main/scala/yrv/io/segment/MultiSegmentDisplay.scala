package yrv.io.segment

import chisel3._
import chisel3.util._

class MultiSegmentDisplay[T](nybbles: T)(implicit num: Integral[T]) extends Module {
  import num._

  var io = IO(new Bundle {
    val nums = Input(Vec(nybbles.toInt, UInt(4.W)))
    val hexes = Output(Vec(nybbles.toInt, UInt(7.W)))
  })

  for ((hex, num) <- io.hexes.zip(io.nums)) {
    val seg = Module(new SegmentDisplay)
    seg.io.num := num
    hex := seg.io.hex
  }
}

object MultiSegmentDisplay {
  def apply(nums: UInt) = {
    val nybbles = (nums.getWidth / 4).ceil.toInt
    val m = Module(new MultiSegmentDisplay(nybbles))
    m.io.nums := nums.asTypeOf(Vec(nybbles, UInt(4.W)))
    m.io.hexes
  }
}
