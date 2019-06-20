package yrv.io.uart

import chisel3._
import chisel3.util._

class UartIo extends Bundle {
  val rx = Input(UInt(1.W))
  val tx = Output(UInt(1.W))
}
