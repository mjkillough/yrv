import types::*;

module decode(
  input logic clk,

  input logic [31:0] instr,

  input logic rd_write,
  inout logic [4:0] rd,
  input logic [63:0] rd_data,

  output control_t control,

  output logic [4:0] rs1,
  output logic [4:0] rs2,
  output logic [63:0] rs1_data,
  output logic [63:0] rs2_data,

  output logic [63:0] imm
);

  regfile regs(.*);

  // decode_immediate?
  immediate_decoder imm_decoder(.*);

  logic [6:0] opcode = instr[6:0];
  logic [2:0] funct3 = instr[14:12];
  logic [6:0] funct7 = instr[31:25];

  decode_alu decode_alu(
    .func(control.alu_func),
    .*
  );

  assign rs1 = instr[19:15];
  assign rs2 = instr[24:20];
  assign rd  = instr[11:7];

  logic op = opcode == OPCODE_OP;
  logic op_imm = opcode == OPCODE_OP_IMM;
  // logic branch = opcode == OPCODE_BRANCH;

  assign control.use_imm  = op_imm;
  assign control.rd_write = op || op_imm;

endmodule

