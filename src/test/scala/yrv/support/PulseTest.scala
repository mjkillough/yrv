package yrv.support

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class PulseTests(c: Pulse) extends PeekPokeTester(c) {
  private val pulse = c

  poke(pulse.io.in, 1)
  expect(pulse.io.out, 0)
  step(1) // in r1
  expect(pulse.io.out, 0)
  step(1) // in r2
  expect(pulse.io.out, 1)

  for (i <- 0 to 10) {
    step(1)
    expect(pulse.io.out, 0)
  }
}

class PulseTester extends ChiselFlatSpec {
  behavior of "Pulse"

  backends foreach {backend =>
    it should s"delays and triggers on edge ($backend)" in {
      Driver(() => new Pulse, backend) {
        c => new PulseTests(c)
      } should be (true)
    }
  }
}
