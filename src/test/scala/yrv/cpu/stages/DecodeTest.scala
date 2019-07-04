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

class DecodeOpTests(c: Decode) extends PeekPokeTester(c) {
  private val decode = c

  val add = InstrR(
    funct7 = 0,
    rs1 = 1,
    rs2 = 2,
    funct3 = 0x6,
    rd = 3,
    opcode = 0x33
  ).encode

  poke(decode.io.in.instr, add)
  poke(decode.io.regs.val1, 2)
  poke(decode.io.regs.val2, 3)

  expect(decode.io.regs.addr1, 1) // rs1, x1
  expect(decode.io.regs.addr2, 2) // rs2, x2
  expect(decode.io.out.val1, 2) // rs1, x1
  expect(decode.io.out.val2, 3) // rs2, x2
  expect(decode.io.control.execute.func, 0x6) // funct3
  expect(decode.io.control.commit.rd, 3) // x3

  val sub = InstrR(
    funct7 = 0x20,
    rs1 = 1,
    rs2 = 2,
    funct3 = 0,
    rd = 3,
    opcode = 0x33
  ).encode

  poke(decode.io.in.instr, sub)
  poke(decode.io.regs.val1, 2)
  poke(decode.io.regs.val2, 3)

  expect(decode.io.regs.addr1, 1) // rs1, x1
  expect(decode.io.regs.addr2, 2) // rs2, x2
  expect(decode.io.out.val1, 2) // rs1, x1
  expect(decode.io.out.val2, 3) // rs2, x2
  expect(decode.io.control.execute.func, 0x8) // funct3
  expect(decode.io.control.commit.rd, 3) // x3
}

class DecodeTester extends ChiselFlatSpec {
  behavior of "Decode"

  backends foreach {backend =>
    it should s"decode OP-IMM instructions successfully ($backend)" in {
      Driver(() => new Decode, backend) {
        c => new DecodeOpImmTests(c)
      } should be (true)
    }

    it should s"decode OP instructions successfully ($backend)" in {
      Driver(() => new Decode, backend) {
        c => new DecodeOpTests(c)
      } should be (true)
    }
  }
}
