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

case class InstrR(funct7: Int, rs2: Int, rs1: Int, funct3: Int, rd: Int, opcode: Int) extends Instr {
  override def encode = {
    (funct7 & 0x7F) << 25 |
      (rs2 & 0x1F) << 20 |
      (rs1 & 0x1F) << 15 |
      (funct3 & 0x7) << 12 |
      (rd & 0x1F) << 7 |
      (opcode & 0x3F)
  }
}