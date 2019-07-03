package yrv.cpu.stages

import chisel3._
import chisel3.util._

class MemoryControl extends Bundle {

}

class MemoryOut extends Bundle {
  val value = UInt(32.W)
}

class Memory extends Module {
  val io = IO(new Bundle {
    val control = Input(new MemoryControl)

    val in = Input(Flipped(new ExecuteOut))
    val out = Output(new MemoryOut)
  })

  io.in <> io.out
}
