package yrv.cpu.stages

import chisel3._
import chisel3.util._

import yrv.cpu.components.Alu

class ExecuteControl extends Bundle {
  val func = UInt(4.W)
}

class ExecuteOut extends Bundle {
  val value = UInt(32.W)
}

class Execute extends Module {
  val io = IO(new Bundle {
    val control = Input(new ExecuteControl)

    val in = Input(new DecodeOut)
    val out = Output(new ExecuteOut)
  })

  val alu = Module(new Alu)
  alu.io.func := io.control.func
  alu.io.in1 := io.in.val1
  alu.io.in2 := io.in.val2

  io.out.value := alu.io.out
}
