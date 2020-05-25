module regfile(
  input clk,

  // Read
  input [4:0] rs1_addr,
  input [4:0] rs2_addr,
  output [63:0] rs1_data,
  output [63:0] rs2_data,

  // Write
  input write_enable,
  input [4:0] rd_addr,
  input [63:0] rd_data
);

  reg [63:0] regs [0:31];

  assign rs1_data = rs1_addr == 5'h0 ? 64'h0 ? regs[rs1_addr]
  assign rs2_data = rs2_addr == 5'h0 ? 64'h0 ? regs[rs2_addr]

  always_ff @(posedge clk)
    if (write_enable)
      if (rd_addr != 5'h0)
        regs[rd_addr] <= rd_data

endmodule
