module regfile(
  input clk,

  // read
  input [4:0] rs1,
  input [4:0] rs2,
  output [63:0] rs1_data,
  output [63:0] rs2_data,

  // write
  input rd_write,
  input [4:0] rd,
  input [63:0] rd_data
);

  reg [63:0] regs [0:31];

  assign rs1_data = rs1 == 5'h0 ? 64'h0 : regs[rs1];
  assign rs2_data = rs2 == 5'h0 ? 64'h0 : regs[rs2];

  always_ff @(posedge clk)
    if (rd_write)
      if (rd != 5'h0)
        regs[rd] <= rd_data;

endmodule
