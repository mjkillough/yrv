typedef enum {
  ALU_FUNC_UNKNOWN,

  ALU_FUNC_ADD,
  ALU_FUNC_SUB,

  ALU_FUNC_AND,
  ALU_FUNC_OR,
  ALU_FUNC_XOR,

  ALU_FUNC_SLL,
  ALU_FUNC_SRL,
  ALU_FUNC_SRA,

  ALU_FUNC_SEQ,
  ALU_FUNC_SLT,
  ALU_FUNC_SLTU
} alu_func /*verilator public*/;

module alu(
  input alu_func func,
  input signed [63:0] in1,
  input signed [63:0] in2,
  output [63:0] out
);

  always_comb
    case (func)
      ALU_FUNC_ADD:  out = in1 + in2;
      ALU_FUNC_SUB:  out = in1 - in2;

      ALU_FUNC_AND:  out = in1 & in2;
      ALU_FUNC_OR:   out = in1 | in2;
      ALU_FUNC_XOR:  out = in1 ^ in2;

      ALU_FUNC_SLL:  out = in1 <<  in2[4:0];
      ALU_FUNC_SRL:  out = in1 >>  in2[4:0];
      ALU_FUNC_SRA:  out = in1 >>> in2[4:0];

      ALU_FUNC_SEQ:  out = {63'h0, in1 == in2};
      ALU_FUNC_SLT:  out = {63'h0, in1 < in2};
      ALU_FUNC_SLTU: out = {63'h0, $unsigned(in1) < $unsigned(in2)};

      default: out = 64'hxxxxxxxxxxxxxxxx;
    endcase

endmodule

