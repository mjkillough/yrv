package yrv.uart

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

object UartTransmitterTests {
  val clocksPerBaud = 3
}

class UartTransmitterTests(c: UartTransmitter) extends PeekPokeTester(c) {
  import UartTransmitterTests._

  private val uart = c

  val data = Integer.parseInt("01110101", 2)
  poke(uart.io.data, data)
  poke(uart.io.write, false)

  // Doesn't do anything until io.write held.
  step(5)
  expect(uart.io.busy, 0, "initial idle")
  expect(uart.io.tx, 1, "initial idle")

  poke(uart.io.write, true)

  // Start bit.
  step(1)
  expect(uart.io.busy, 1, "start")
  expect(uart.io.tx, 0, "start")
  poke(uart.io.write, false)

  for (bit <- 0 to 3) {
    val expected = (data >> bit) & 0x1

    step(clocksPerBaud)
    expect(uart.io.busy, 1, s"bit $bit: busy")
    expect(uart.io.tx, expected, s"bit $bit: data")
  }

  // Changing data shouldn't affect output while busy:
  poke(uart.io.data, 0)
  poke(uart.io.write, true)

  for (bit <- 4 to 7) {
    val expected = (data >> bit) & 0x1

    step(clocksPerBaud)
    expect(uart.io.busy, 1, s"bit $bit: busy")
    expect(uart.io.tx, expected, s"bit $bit: data")
  }

  // Stop bit
  step(clocksPerBaud)
  expect(uart.io.busy, 1, "stop bit")
  expect(uart.io.tx, 1, "stop bit")
  // Turn this off before we go back to sIdle:
  poke(uart.io.write, false)

  // ... and done. Stay idle, with tx high.
  step(clocksPerBaud)
  expect(uart.io.busy, 0, "final idle 1")
  expect(uart.io.tx, 1, "final idle 1")
  step(100)
  expect(uart.io.busy, 0, "final idle 2")
  expect(uart.io.tx, 1, "findle idle 2")
}

class UartTransmitterTester extends ChiselFlatSpec {
  behavior of "UartTransmitter"

  backends foreach {backend =>
    it should s"transmits value ($backend)" in {
      Driver(() => new UartTransmitter(UartTransmitterTests.clocksPerBaud), backend) {
        c => new UartTransmitterTests(c)
      } should be (true)
    }
  }
}
