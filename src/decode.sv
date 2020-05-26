import types::*;

module decode(
  input logic clk,

  input logic [31:0] instr,

  input logic rd_write,
  // Need to think about unique name for this.
  input logic [4:0] rd,
  input logic [63:0] rd_data,

  output control_t control,

  // Rename rs1_addr etc. to rs1?
  output logic [4:0] rs1_addr,
  output logic [4:0] rs2_addr,
  output logic [4:0] rd_addr,
  output logic [63:0] rs1_data,
  output logic [63:0] rs2_data,

  output logic [63:0] imm
);

  regfile regs(.rd_addr(rd), .*);

  // decode_immediate?
  immediate_decoder imm_decoder(.*);

  // decode_alu?

  assign rs1_addr = instr[19:15];
  assign rs2_addr = instr[24:20];
  assign rd_addr  = instr[11:7];

  logic [6:0] opcode = instr[6:0];
  logic [2:0] func3  = instr[14:12];

  logic op_imm = opcode == 7'b0010011;

  always_comb
    casez ({instr[30], func3})
      4'b0000: control.alu_func = ALU_FUNC_ADD;
      4'b1000: control.alu_func = ALU_FUNC_SUB;
      default: control.alu_func = ALU_FUNC_UNKNOWN;
    endcase

  assign control.use_imm  = op_imm;
  assign control.rd_write = op_imm;
  assign control.alu_func = ALU_FUNC_ADD;

endmodule

