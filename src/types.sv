`define FUNCT3_ADD  3'b000 // ADD, SUB
`define FUNCT3_SLL  3'b001
`define FUNCT3_SLT  3'b010
`define FUNCT3_SLTU 3'b011
`define FUNCT3_XOR  3'b100
`define FUNCT3_SR   3'b101 // SRL, SRA
`define FUNCT3_OR   3'b110
`define FUNCT3_AND  3'b111

`define FUNCT3_BEQ  3'b000
`define FUNCT3_BNE  3'b001
`define FUNCT3_BLT  3'b100
`define FUNCT3_BGE  3'b101
`define FUNCT3_BLTU 3'b110
`define FUNCT3_BGEU 3'b111

package types;

  typedef enum [6:0] {
    OPCODE_OP_IMM   = 7'b0010011,
    OPCODE_OP_IMM32 = 7'b0011011,
    OPCODE_OP       = 7'b0110011,
    OPCODE_BRANCH   = 7'b1100011,
    OPCODE_JALR     = 7'b1100111,
    OPCODE_JAL      = 7'b1101111
  } opcode_t;

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
  } alu_func_t;

  typedef enum {
    // Don't branch.
    BRANCH_NONE,
    // Relative branch if ALU result is non-zero.
    BRANCH_TRUE,
    // Relative branch if ALU result is zero.
    BRANCH_FALSE,
    // Relative branch (JAL) always.
    BRANCH_ALWAYS,
    // Indirect branch (JALR) always.
    BRANCH_INDIRECT
  } branch_t;

  typedef enum {
    // Don't write anything to the register file.
    WRITEBACK_NONE,
    // Write the result of the ALU operation.
    WRITEBACK_ALU,
    // Write the next PC.
    WRITEBACK_PC
  } writeback_t;

  typedef struct packed {
    bit use_imm;
    alu_func_t alu_func;
    branch_t branch;
    writeback_t writeback;
  } control_t;

endpackage

