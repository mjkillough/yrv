package yrv.cpu.components

import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}


class AluTests(c: Alu) extends PeekPokeTester(c) {
  private val alu = c

  case class Case(func: Int, in1: Int, in2: Int, out: Int)

  val FUNC_ADD = 0x0
  val FUNC_SLT = 0x2

  val tests = Seq(
    Case(FUNC_ADD, in1 = 1, in2 = 3, out = 4),
    Case(FUNC_ADD, in1 = Int.MaxValue, in2 = 1, out = Int.MaxValue + 1),

    Case(FUNC_SLT, in1 = 1, in2 = 2, out = 1),
    Case(FUNC_SLT, in1 = 2, in2 = 1, out = 0)

    // TODO
  )

  for (test <- tests) {
    val in1 = intToUnsignedBigInt(test.in1)
    val in2 = intToUnsignedBigInt(test.in2)
    val out = intToUnsignedBigInt(test.out)

    poke(alu.io.func, test.func)
    poke(alu.io.in1, in1)
    poke(alu.io.in2, in2)
    expect(alu.io.out, out, s"ALU func=${test.func} in1=${in1} in2=${in2}")
  }
}

class AluTester extends ChiselFlatSpec {
  behavior of "Alu"

  backends foreach {backend =>
    it should s"performs functions ($backend)" in {
      Driver(() => new Alu, backend) {
        c => new AluTests(c)
      } should be (true)
    }
  }
}
