package yrv.cpu.components

import chisel3._
import chisel3.util._

class RegFileReadIo extends Bundle {
  val addr1 = Input(UInt(5.W))
  val addr2 = Input(UInt(5.W))
  val val1 = Output(UInt(32.W))
  val val2 = Output(UInt(32.W))
}

class RegFileWriteIo extends Bundle {
  val addr = Input(UInt(5.W))
  val value = Input(UInt(32.W))
  val enable = Input(Bool())
}

class RegFile extends Module {
  val io = IO(new Bundle {
    val read = new RegFileReadIo
    val write = new RegFileWriteIo
  })

  val mem = Mem(32, UInt(32.W))

  io.read.val1 := Mux(io.read.addr1 =/= 0.U, mem(io.read.addr1), 0.U)
  io.read.val2 := Mux(io.read.addr2 =/= 0.U, mem(io.read.addr2), 0.U)

  when (io.write.enable) {
    mem(io.write.addr) := io.write.value
  }
}
