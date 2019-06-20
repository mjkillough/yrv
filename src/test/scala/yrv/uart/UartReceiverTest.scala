package yrv.uart

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

object UartReceiverTests {
  val clocksPerBaud = 3
}

class UartReceiverTests(c: UartReceiver) extends PeekPokeTester(c) {
  import UartReceiverTests._

  private val uart = c

  val data = Integer.parseInt("01110101", 2)

  // Holding io.rx high shouldn't start state machine.
  poke(uart.io.rx, 1)
  step(clocksPerBaud * 9)
  expect(uart.io.ready, false, s"ready initial")

  // Start bit.
  poke(uart.io.rx, 0)
  step(clocksPerBaud)
  expect(uart.io.ready, false, s"ready start bit")

  for (bit <- 0 to 7) {
    val next = (data >> bit) & 0x1

    poke(uart.io.rx, next)
    step(clocksPerBaud)

    expect(uart.io.ready, false, s"ready bit $bit")
  }

  // Stop bit.
  poke(uart.io.rx, 1)
  step(clocksPerBaud)

  expect(uart.io.ready, true, s"ready")
  expect(uart.io.data, data)

  // Stays ready until clear.
  step(clocksPerBaud)
  expect(uart.io.ready, true, s"still ready")
  poke(uart.io.clear, true)
  step(1)
  expect(uart.io.ready, false, s"no longer ready")
}

class UartReceiverTester extends ChiselFlatSpec {
  behavior of "UartReceiver"

  backends foreach {backend =>
    it should s"receives value ($backend)" in {
      Driver(() => new UartReceiver(UartReceiverTests.clocksPerBaud), backend) {
        c => new UartReceiverTests(c)
      } should be (true)
    }
  }
}
