package yrv.cpu

import chisel3._
import chisel3.util._

import yrv.cpu.components.RegFile
import yrv.cpu.stages._

class Cpu extends Module {
  val io = IO(new Bundle {
    val instr = Input(UInt(32.W))
  })

  val regs = Module(new RegFile)

  val decode = Module(new Decode)
  val execute = Module(new Execute)
  val memory = Module(new Memory)
  val commit = Module(new Commit)

  decode.io.in.instr := io.instr

  decode.io.regs <> regs.io.read
  commit.io.regs <> regs.io.write

  execute.io.in <> decode.io.out
  memory.io.in <> execute.io.out
  commit.io.in <> memory.io.out

  execute.io.control <> decode.io.control.execute
  memory.io.control <> decode.io.control.memory
  commit.io.control <> decode.io.control.commit
}
