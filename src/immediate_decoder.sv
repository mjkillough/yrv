import types::*;

module immediate_decoder(
  input [31:0] instr,
  output [63:0] imm
);

  logic [6:0] opcode = instr[6:0];

  always_comb
    case (opcode)
      // I
      OPCODE_OP_IMM,
      OPCODE_OP_IMM32,
      OPCODE_JALR:
        imm = { {53{instr[31]}}, instr[30:20] };

      // B
      OPCODE_BRANCH:
        imm = { {52{instr[31]}}, instr[7], instr[30:25], instr[11:8], 1'b0 };

      // J
      OPCODE_JAL:
        imm = { {44{instr[21]}}, instr[19:12], instr[20], instr[30:25], instr[24:21], 1'b0 };

      default:  imm = 64'd0;
    endcase

endmodule

