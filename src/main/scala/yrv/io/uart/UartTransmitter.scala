package yrv.io.uart

import chisel3._
import chisel3.util._

class UartTransmitter(clocksPerBaud: Int) extends Module {
  var io = IO(new Bundle {
    val write = Input(Bool())
    val data = Input(UInt(8.W))
    val busy = Output(Bool())
    val tx = Output(UInt(1.W))
  })

  val counterInitial = (clocksPerBaud - 1).asUInt
  val counter = RegInit(counterInitial)
  val baud = counter === 0.U

  val sIdle :: sInitial :: s0::s1::s2::s3::s4::s5::s6::s7 :: sLast :: Nil = Enum(11)
  val state = RegInit(sIdle)

  val busy = RegInit(false.B)
  val shift = RegInit("b111111111".U) // 8 + 1 bits

  io.tx := shift(0)
  io.busy := state =/= sIdle

  when (state === sIdle) {
    when (io.write) {
      state := sInitial
      shift := Cat(io.data, 0.U)
    }
  } .otherwise {
    counter := counter - 1.U
  }

  when (baud) {
    counter := counterInitial

    when (state > sIdle && state < sLast) {
      state := state + 1.U
      shift := Cat(1.U, shift(8, 1))
    } .otherwise {
      state := sIdle
    }
  }
}
