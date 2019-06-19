package yrv

import chisel3._
import chisel3.util._

class SegmentDisplay extends Module {
  var io = IO(new Bundle {
    val num = Input(UInt(4.W))
    val hex = Output(UInt(7.W))
  })

  // hex is {g,f,e,d,c,b,a} active low
  //
  // ---a----
  // |      |
  // f      b
  // |      |
  // ---g----
  // |      |
  // e      c
  // |      |
  // ---d----

  io.hex := ~"b0000000".asUInt(7.W)

  switch (io.num) {
    is (0.U)  { io.hex := ~"b0111111".asUInt(7.W) }
    is (1.U)  { io.hex := ~"b0000110".asUInt(7.W) }
    is (2.U)  { io.hex := ~"b1011011".asUInt(7.W) }
    is (3.U)  { io.hex := ~"b1001111".asUInt(7.W) }
    is (4.U)  { io.hex := ~"b1100110".asUInt(7.W) }
    is (5.U)  { io.hex := ~"b1101101".asUInt(7.W) }
    is (6.U)  { io.hex := ~"b1111101".asUInt(7.W) }
    is (7.U)  { io.hex := ~"b0000111".asUInt(7.W) }
    is (8.U)  { io.hex := ~"b1111111".asUInt(7.W) }
    is (9.U)  { io.hex := ~"b1101111".asUInt(7.W) }
    is (10.U) { io.hex := ~"b1110111".asUInt(7.W) }
    is (11.U) { io.hex := ~"b1111100".asUInt(7.W) }
    is (12.U) { io.hex := ~"b0111001".asUInt(7.W) }
    is (13.U) { io.hex := ~"b1011110".asUInt(7.W) }
    is (14.U) { io.hex := ~"b1111001".asUInt(7.W) }
    is (15.U) { io.hex := ~"b1110001".asUInt(7.W) }
  }
}
