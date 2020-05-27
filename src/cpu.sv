import types::control_t;

module cpu(
  input logic clk,
  input [31:0] instr
);
  /* verilator lint_off UNUSED */

  logic [63:0] pc;

  logic [4:0] rs1, rs2, rd;
  logic [63:0] rs1_data, rs2_data, imm;

  logic rd_write = 0;
  logic [63:0] rd_data = 0;

  control_t control;

  decode decode(.*);

  logic signed [63:0] in1;
  logic signed [63:0] in2;
  logic [63:0] out;

  assign in1 = rs1_data;
  assign in2 = control.use_imm ? imm : rs2_data;

  alu alu(
    .func(control.alu_func),
    .*
  );

  always_ff @(posedge clk)
    if (control.branch == BRANCH_TRUE && out > 64'b0)
      pc <= imm;
    else if (control.branch == BRANCH_FALSE && out == 64'b0)
      pc <= imm;
    else
      pc <= pc + 64'h4;

  assign rd_write = control.rd_write;
  assign rd_data = out;

  /* verilator lint_on UNUSED */

  always_ff @(posedge clk) begin
    $display("instr=%b", instr);
    $display("in1=%d in2=%d out=%d", in1, in2, out);

    $display("branch=%s", control.branch);

    $display("rs1=%d rs2=%d rd=%d rs1_data=%d rs2_data=%d rd_data=%d imm=%d", rs1, rs2, rd, rs1_data, rs2_data, rd_data, imm);
  end

endmodule

