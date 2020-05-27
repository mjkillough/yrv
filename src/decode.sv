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

  // logic op = opcode == OPCODE_OP;
  logic op_imm = opcode == OPCODE_OP_IMM;
  logic branch = opcode == OPCODE_BRANCH;
  logic jal = opcode == OPCODE_JAL;
  logic jalr = opcode == OPCODE_JALR;

  always_comb
    if (branch)
      case (funct3)
        `FUNCT3_BEQ:  control.branch = BRANCH_TRUE;
        `FUNCT3_BNE:  control.branch = BRANCH_FALSE;
        `FUNCT3_BLT:  control.branch = BRANCH_TRUE;
        `FUNCT3_BGE:  control.branch = BRANCH_FALSE;
        `FUNCT3_BLTU: control.branch = BRANCH_TRUE;
        `FUNCT3_BGEU: control.branch = BRANCH_FALSE;
        default:      control.branch = BRANCH_NONE;
      endcase
    else if (jal)
      control.branch = BRANCH_ALWAYS;
    else if (jalr)
      control.branch = BRANCH_INDIRECT;
    else
      control.branch = BRANCH_NONE;

  always_comb
    case (opcode)
      OPCODE_OP:     control.writeback = WRITEBACK_ALU;
      OPCODE_OP_IMM: control.writeback = WRITEBACK_ALU;
      OPCODE_JAL:    control.writeback = WRITEBACK_PC;
      OPCODE_JALR:   control.writeback = WRITEBACK_PC;
      default:       control.writeback = WRITEBACK_NONE;
    endcase

  assign control.use_imm = op_imm || jal || jalr;

endmodule

