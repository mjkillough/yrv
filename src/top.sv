module top(
  input CLOCK_50,
  input  [17:0] SW,
  input  /*[3:0]*/ KEY,
  output /*[17:0]*/ [2:0] LEDR,
  output [6:0] HEX0, HEX1 /*, HEX2, HEX3*/,

  input UART_RXD,
  output UART_TXD
);

  reg [7:0] data = 0;
  wire ready, resetn;

  assign resetn = !SW;
  assign LEDR[0] = ready;
  assign LEDR[2] = resetn;

  segment_display hex0(
    .num(data[3:0]),
    .hex(HEX0)
  );
  segment_display hex1(
    .num(data[7:4]),
    .hex(HEX1)
  );

  uart_rx#(
    .CLOCK_HZ(50000000),
    .BAUD_RATE(115200)
  ) rx(
    .resetn(resetn),
    .clk(CLOCK_50),
    .rx(UART_RXD),
    .ready(ready),
    .data(data)
  );
  uart_tx#(
    .CLOCK_HZ(50000000),
    .BAUD_RATE(115200)
  ) tx(
    .resetn(resetn),
    .clk(CLOCK_50),
    .tx(UART_TXD),
    .write(ready),
    .data(data),
    .busy(LEDR[1])
  );

endmodule
