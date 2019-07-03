package yrv.cpu.stages

import chisel3._
import chisel3.util._

import yrv.cpu.components.RegFileReadIo

class Control extends Bundle {
  val execute = new ExecuteControl
  val memory = new MemoryControl
  val commit = new CommitControl
}

class DecodeIn extends Bundle {
  val instr = UInt(32.W)
}

class DecodeOut extends Bundle {
  val val1 = UInt(32.W)
  val val2 = UInt(32.W)
}

class Decode extends Module {
  import Decode._

  val io = IO(new Bundle {
    val regs = Flipped(new RegFileReadIo)

    val in = Input(new DecodeIn)
    val out = Output(new DecodeOut)
    val control = Output(new Control)
  })

  val instr = io.in.instr

  val opcode = instr(6, 0)
  val func = instr(14, 12)

  val rs1 = instr(19, 15)
  val rs2 = instr(24, 20)
  val rd = instr(11, 7)

  val useImm = opcode === OPCODE_OP_IMM

  val rType :: iType :: sType :: bType :: uType :: jType :: nil = Enum(6)
  val ty = iType

  // Sign-extend immediates:
  val imm = Wire(SInt(32.W))
  imm := 0.S
  switch (ty) {
    is (iType) {
      imm := Cat(instr(31), instr(30, 20)).asSInt
    }
  }

  io.regs.addr1 := rs1
  io.regs.addr2 := rs2
  io.out.val1 := io.regs.val1
  io.out.val2 := Mux(useImm, imm.asUInt, io.regs.val2)

  io.control.execute.func := func
  io.control.commit.write := true.B
  io.control.commit.rd := rd
}

object Decode {
  val OPCODE_OP_IMM = "b0010011".U
  val OPCODE_OP     = "b0110011".U
}