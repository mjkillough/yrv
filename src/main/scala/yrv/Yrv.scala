package yrv

import chisel3._
import chisel3.util._

import yrv.io.uart._
import yrv.io.segment._

class Yrv extends Module {
  val io = IO(new Bundle {
    val hexes = Output(Vec(2, UInt(7.W)))

    val uart = new UartIo
    val led = Output(Bool())
    val button = Input(Bool())
    // val data = Input(UInt(16.W))
  })

  val uartRx = Module(new UartReceiver(5208))
  uartRx.io.rx := io.uart.rx

  val write = io.button && uartRx.io.ready
  uartRx.io.clear := write

  val uartTx = Module(new UartTransmitter(5208))
  uartTx.io.write := write
  uartTx.io.data := uartRx.io.data
  io.uart.tx := uartTx.io.tx
  io.led := uartTx.io.busy

  io.hexes := MultiSegmentDisplay(uartRx.io.data)
}

object Yrv extends App {
  chisel3.Driver.execute(args, () => new Yrv)
}
