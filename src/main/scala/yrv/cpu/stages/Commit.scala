package yrv.cpu.stages

import chisel3._
import chisel3.util._

import yrv.cpu.components.RegFileWriteIo

class CommitControl extends Bundle {
  val write = Bool()
  val rd = UInt(5.W)
}

class Commit extends Module {
  val io = IO(new Bundle {
    val control = Input(new CommitControl)
    val regs = Flipped(new RegFileWriteIo)

    val in = Input(Flipped(new MemoryOut))
  })

  io.regs.enable := io.control.write
  io.regs.addr := io.control.rd
  io.regs.value := io.in.value
}
