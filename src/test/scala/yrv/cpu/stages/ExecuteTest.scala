package yrv.cpu.stages

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class ExecuteTests(c: Execute) extends PeekPokeTester(c) {
  private val execute = c

  poke(execute.io.control.func, 0) // FUNC_ADD
  poke(execute.io.in.val1, 1)
  poke(execute.io.in.val2, 2)
  expect(execute.io.out.value, 3)
}

class ExecuteTester extends ChiselFlatSpec {
  behavior of "Execute"

  backends foreach {backend =>
    it should s"calls ALU ($backend)" in {
      Driver(() => new Execute, backend) {
        c => new ExecuteTests(c)
      } should be (true)
    }
  }
}
