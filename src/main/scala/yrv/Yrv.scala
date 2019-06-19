package yrv

import chisel3._
import chisel3.util._

class Yrv extends Module {
  val io = IO(new Bundle {
    val sw = Input(UInt(4.W))
    val hex0 = Output(UInt(7.W))
    val hex1 = Output(UInt(7.W))
  })

  val seg1 = Module(new SegmentDisplay)
  seg1.io.num := io.sw
  io.hex0 := seg1.io.hex

  io.hex1 := 0.U
}

object Yrv extends App {
  chisel3.Driver.execute(args, () => new Yrv)
}
