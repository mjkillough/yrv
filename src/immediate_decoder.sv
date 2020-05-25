module immediate_decoder(
  input [31:0] instr,
  output [63:0] imm
);

  logic [4:0] opcode = instr[6:2];

  always_comb
    casez (opcode)
      // I
      5'b001?0: imm = { {53{instr[31]}}, instr[30:20] };

      default:  imm = 64'd0;
    endcase

endmodule

