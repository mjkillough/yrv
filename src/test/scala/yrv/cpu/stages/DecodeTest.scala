package yrv.cpu.stages

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

import yrv.cpu.instr._

class DecodeOpImmTests(c: Decode) extends PeekPokeTester(c) {
  private val decode = c

  val instr = InstrI(
    imm = 1,
    rs1 = 1,
    funct3 = 0x6,
    rd = 2,
    opcode = 0x13
  ).encode

  poke(decode.io.in.instr, instr)
  poke(decode.io.regs.val1, 2)

  expect(decode.io.regs.addr1, 1) // rs1, x1
  expect(decode.io.out.val1, 2) // rs1, x1
  expect(decode.io.out.val2, 1) // imm
  expect(decode.io.control.execute.func, 0x6) // funct3
  expect(decode.io.control.commit.rd, 2) // x2
}

class DecodeTester extends ChiselFlatSpec {
  behavior of "Decode"

  backends foreach {backend =>
    it should s"decode OP-IMM instructions successfully ($backend)" in {
      Driver(() => new Decode, backend) {
        c => new DecodeOpImmTests(c)
      } should be (true)
    }
  }
}
