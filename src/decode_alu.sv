import types::*;

module decode_alu(
  input logic [6:0] opcode,
  input logic [2:0] funct3,
  input logic [6:0] funct7,
  output alu_func_t func
);

  logic op = opcode == OPCODE_OP;
  logic op_imm = opcode == OPCODE_OP_IMM;
  logic branch = opcode == OPCODE_BRANCH;
  logic jalr = opcode == OPCODE_JALR;

  always_comb
    if (op || op_imm)
      case (funct3)
        `FUNCT3_SLT:  func = ALU_FUNC_SLT;
        `FUNCT3_SLTU: func = ALU_FUNC_SLTU;
        `FUNCT3_XOR:  func = ALU_FUNC_XOR;
        `FUNCT3_AND:  func = ALU_FUNC_AND;

        `FUNCT3_ADD:
          if (op_imm || !funct7[5])
            func = ALU_FUNC_ADD;
          else
            func = ALU_FUNC_SUB;

        `FUNCT3_SR:
          if (!funct7[5])
            func = ALU_FUNC_SRL;
          else
            func = ALU_FUNC_SRA;

        default: func = ALU_FUNC_UNKNOWN;
      endcase

    else if (branch)
      case (funct3)
        `FUNCT3_BEQ:  func = ALU_FUNC_SEQ;
        `FUNCT3_BNE:  func = ALU_FUNC_SEQ;
        `FUNCT3_BLT:  func = ALU_FUNC_SLT;
        `FUNCT3_BGE:  func = ALU_FUNC_SLT;
        `FUNCT3_BLTU: func = ALU_FUNC_SLTU;
        `FUNCT3_BGEU: func = ALU_FUNC_SLTU;
        default:      func = ALU_FUNC_UNKNOWN;
      endcase

    else if (jalr)
      func = ALU_FUNC_ADD;

  wire _verilator_unused_ok = &{
    1'b0,
    funct7[6],
    funct7[4:0],
    1'b0
  };

endmodule

