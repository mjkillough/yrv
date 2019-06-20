package yrv

import chisel3._
import chisel3.util._

import yrv.uart._

class Yrv extends Module {
  val io = IO(new Bundle {
    val hex0 = Output(UInt(7.W))
    val hex1 = Output(UInt(7.W))

    val uart = new UartIo
    val led = Output(Bool())
    val button = Input(Bool())
    val data = Input(UInt(8.W))

  })

  val seg1 = Module(new SegmentDisplay)
  seg1.io.num := io.data(3, 0)
  io.hex0 := seg1.io.hex
  val seg2 = Module(new SegmentDisplay)
  seg2.io.num := io.data(7, 4)
  io.hex1 := seg2.io.hex

  val last = RegInit(false.B)
  val triggered = io.button && !last
  last := io.button

  val uartTx = Module(new UartTransmitter(5208))
  uartTx.io.write := triggered
  uartTx.io.data := io.data
  io.uart.tx := uartTx.io.tx
  io.led := uartTx.io.busy
}

object Yrv extends App {
  chisel3.Driver.execute(args, () => new Yrv)
}
