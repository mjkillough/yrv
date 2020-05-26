package types;

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

  typedef struct packed {
    bit use_imm;
    bit rd_write;
    alu_func_t alu_func;
  } control_t;

endpackage

