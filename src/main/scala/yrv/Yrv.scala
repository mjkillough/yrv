package yrv

import chisel3._
import chisel3.util._

import yrv.uart._

class Yrv extends Module {
  val io = IO(new Bundle {
    val sw = Input(UInt(4.W))
    val sw2 = Input(UInt(8.W))
    val sw3 = Input(Bool())
    val uart = new UartIo
    val hex0 = Output(UInt(7.W))
    val hex1 = Output(UInt(7.W))
    val led = Output(Bool())
  })

  val seg1 = Module(new SegmentDisplay)
  seg1.io.num := io.sw
  io.hex0 := seg1.io.hex

  io.hex1 := 0.U
  io.uart.tx := 0.U

  val uartTx = Module(new UartTransmitter(5208))
  uartTx.io.write := io.sw3
  uartTx.io.data := io.sw2
  io.uart.tx := uartTx.io.tx
  io.led := uartTx.io.busy

}

object Yrv extends App {
  chisel3.Driver.execute(args, () => new Yrv)
}
