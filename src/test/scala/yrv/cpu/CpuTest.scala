package yrv.cpu

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

import yrv.cpu.instr._

class CpuTests(c: Cpu) extends PeekPokeTester(c) {
  private val cpu = c

  val instr = InstrI(
    imm = 1,
    rs1 = 1,
    funct3 = 0, // FUNC_ADD
    rd = 2,
    opcode = 0x13 // OP-IMM
  ).encode

  poke(cpu.io.instr, instr)

  // TODO: How to expect?
}

class CpuTester extends ChiselFlatSpec {
  behavior of "Cpu"

  backends foreach {backend =>
    it should s"executes ($backend)" in {
      Driver(() => new Cpu, backend) {
        c => new CpuTests(c)
      } should be (true)
    }
  }
}
