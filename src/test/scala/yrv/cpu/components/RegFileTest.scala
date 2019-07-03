package yrv.cpu.components

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class RegFileTests(c: RegFile) extends PeekPokeTester(c) {
  private val reg = c

  // x1 = 1
  poke(reg.io.write.addr, 1)
  poke(reg.io.write.value, 1)
  poke(reg.io.write.enable, true)
  step(1)

  // x2 = 2
  poke(reg.io.write.addr, 2)
  poke(reg.io.write.value, 2)
  poke(reg.io.write.enable, true)
  step(1)

  // x1 == 1 && x2 == 2
  poke(reg.io.read.addr1, 1)
  expect(reg.io.read.val1, 1)
  poke(reg.io.read.addr2, 2)
  expect(reg.io.read.val2, 2)
  step(1)

  // x1 = 3, enable=false (ignored)
  poke(reg.io.write.addr, 1)
  poke(reg.io.write.value, 3)
  poke(reg.io.write.enable, false)
  step(1)

  // x1 == 1
  poke(reg.io.read.addr1, 1)
  expect(reg.io.read.val1, 1)
  step(1)

  // x0 = 3 (ignored)
  poke(reg.io.write.addr, 0)
  poke(reg.io.write.value, 3)
  poke(reg.io.write.enable, true)
  step(1)

  // x0 == 0
  poke(reg.io.read.addr1, 0)
  expect(reg.io.read.val1, 0)
  step(1)
}

class RegFileTester extends ChiselFlatSpec {
  behavior of "RegFile"

  backends foreach {backend =>
    it should s"read and write successfully ($backend)" in {
      Driver(() => new RegFile, backend) {
        c => new RegFileTests(c)
      } should be (true)
    }
  }
}
