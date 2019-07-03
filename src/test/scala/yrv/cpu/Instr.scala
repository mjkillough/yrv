package yrv.cpu.instr

abstract class Instr {
  def encode: Int
}

case class InstrI(imm: Int, rs1: Int, funct3: Int, rd: Int, opcode: Int) extends Instr {
  override def encode = {
    (imm & 0xFFF) << 20 |
      (rs1 & 0x1F) << 15 |
      (funct3 & 0x7) << 12 |
      (rd & 0x1F) << 7 |
      (opcode & 0x3F)
  }
}
