module uart_baud#(
  parameter CLOCK_HZ = 10,
  parameter BAUD_RATE = 1
)(
  input resetn,
  input clk,
  input half,
  output tick
);

  // Clock Rate Hz / Baud Rate
  // The default value gives 10 clocks per baud.
  wire [31:0] clocks_per_baud = (CLOCK_HZ / BAUD_RATE);

  reg [31:0] clock_count = 0;

  always_ff @(posedge clk, negedge resetn) begin
    if (!resetn) clock_count <= 0;
    else begin
      if (half)
        clock_count <= clocks_per_baud / 32'd2;
      else if (tick)
        clock_count <= 0;
      else
        clock_count <= clock_count + 1'b1;
    end
  end

  assign tick = clock_count == clocks_per_baud - 1;

endmodule

