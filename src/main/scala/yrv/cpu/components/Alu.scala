package yrv.cpu.components

import chisel3._
import chisel3.util._

object Alu {
  val FUNC_ADD  = "b0000".asUInt(4.W)
  val FUNC_SLT  = "b0010".asUInt(4.W)
  val FUNC_SLTU = "b0011".asUInt(4.W)
  val FUNC_AND  = "b0111".asUInt(4.W)
  val FUNC_OR   = "b0110".asUInt(4.W)
  val FUNC_XOR  = "b0100".asUInt(4.W)

  def boolToSInt(value: Bool): SInt = {
    // Add 0 in MSB to avoid sign-extending true.B
    Cat(0.U, value).asSInt
  }
}

class Alu extends Module {
  import Alu._

  val io = IO(new Bundle {
    val func = Input(UInt(4.W))

    val in1 = Input(UInt(32.W))
    val in2 = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val sIn1 = Wire(SInt(32.W))
  val sIn2 = Wire(SInt(32.W))
  sIn1 := io.in1.asSInt
  sIn2 := io.in2.asSInt

  val sOut = Wire(SInt(32.W))
  sOut := 0.S

  switch (io.func) {
    is (FUNC_ADD) {
      sOut := sIn1 +& sIn2
    }
    is (FUNC_SLT) {
      sOut := boolToSInt(sIn1 < sIn2)
    }
    is (FUNC_SLTU) {
      sOut := boolToSInt(io.in1 < io.in2)
    }
    is (FUNC_AND) {
      sOut := sIn1 & sIn2
    }
    is (FUNC_OR) {
      sOut := sIn1 | sIn2
    }
    is (FUNC_XOR) {
      sOut := sIn1 ^ sIn2
    }
  }

  io.out := sOut.asUInt
}
