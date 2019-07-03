package yrv.cpu.stages

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class CommitTests(c: Commit) extends PeekPokeTester(c) {
  private val commit = c

  // write true
  poke(commit.io.in.value, 3)
  poke(commit.io.control.write, true)
  poke(commit.io.control.rd, 5) // x5
  expect(commit.io.regs.value, 3)
  expect(commit.io.regs.addr, 5) // x5
  expect(commit.io.regs.enable, true)

  // write false
  poke(commit.io.in.value, 2)
  poke(commit.io.control.write, false)
  poke(commit.io.control.rd, 4) // x4
  expect(commit.io.regs.value, 2)
  expect(commit.io.regs.addr, 4) // x4
  expect(commit.io.regs.enable, false)
}

class CommitTester extends ChiselFlatSpec {
  behavior of "Commit"

  backends foreach {backend =>
    it should s"commits results ($backend)" in {
      Driver(() => new Commit, backend) {
        c => new CommitTests(c)
      } should be (true)
    }
  }
}
