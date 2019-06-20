package yrv.uart

import chisel3._
import chisel3.util._

class UartReceiver(clocksPerBaud: Int) extends Module {
  var io = IO(new Bundle {
    val rx = Input(UInt(1.W))
    val clear = Input(Bool())
    val ready = Output(Bool())
    val data = Output(UInt(8.W))
  })

  val counterInitial = (clocksPerBaud - 1).asUInt
  val counter = RegInit(counterInitial)
  val baud = counter === 0.U

  val sIdle :: sInitial :: s0::s1::s2::s3::s4::s5::s6::s7 :: sLast :: Nil = Enum(11)
  val state = RegInit(sIdle)

  val buffer = RegInit(0.asUInt(8.W))
  val data = RegInit(0.asUInt(8.W))
  val ready = RegInit(false.B)

  io.ready := ready
  io.data := data

  when (io.clear) {
    ready := false.B
  }

  // Detect start bit.
  when (state === sIdle) {
    when (io.rx === 0.U) {
      state := sInitial
      // Resync so that baud triggers in the middle of each bit.
      counter := counterInitial / 2.U
    }
  } .otherwise {
    when (baud) {
      counter := counterInitial

      when (state < sLast) {
        state := state + 1.U

        // Shift everything after the start-bit into our register.
        when (state > sInitial) {
          buffer := Cat(io.rx, buffer(7, 1))
        }
      } .otherwise {
        state := sIdle
        data := buffer
        ready := true.B
      }
    } .otherwise {
      counter := counter - 1.U
    }
  }
}
