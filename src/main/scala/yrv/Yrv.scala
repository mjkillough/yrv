package yrv

import chisel3._
import chisel3.util._

import yrv.uart._

class Yrv extends Module {
  val io = IO(new Bundle {
    val hexes = Output(Vec(4, UInt(7.W)))

    val uart = new UartIo
    val led = Output(Bool())
    val button = Input(Bool())
    val data = Input(UInt(16.W))

  })

  io.hexes := MultiSegmentDisplay(io.data)

  val uartTx = Module(new UartTransmitter(5208))
  uartTx.io.write := Pulse(io.button)
  uartTx.io.data := io.data(7, 0)
  io.uart.tx := uartTx.io.tx
  io.led := uartTx.io.busy
}

object Yrv extends App {
  chisel3.Driver.execute(args, () => new Yrv)
}
