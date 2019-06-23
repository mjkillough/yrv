package yrv

import chisel3._
import chisel3.util._

class WishboneIo(addrWidth: Int = 8, dataWidth: Int = 8) extends Bundle {
  val addr = Input(UInt(addrWidth.W))
  val dataIn = Input(UInt(dataWidth.W))

  val write = Input(Bool())
  val strobe = Input(Bool())
  val cycle = Input(Bool())

  val dataOut = Output(UInt(dataWidth.W))
  val ack = Output(Bool())
}

class SimpleWishboneSlave(dataWidth: Int = 8) extends Module {
  val io = IO(new WishboneIo)

  val mem = SyncReadMem(256, UInt(dataWidth.W))

  io.ack := false.B
  io.dataOut := 0.U

  when (io.cycle && io.strobe) {
    when (io.write) {
      mem.write(io.addr, io.dataIn)
      io.ack := true.B
    } .otherwise {
      io.dataOut := mem.read(io.addr)
      io.ack := true.B
    }
  }
}
