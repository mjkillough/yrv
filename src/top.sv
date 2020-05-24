`include "uart_rx.sv"
`include "uart_tx.sv"
`include "segment_display.sv"

module top(
  input CLOCK_50,
  /*input  [17:0] SW,*/
  /*input  [3:0] KEY,*/
  output /*[17:0]*/ [1:0] LEDR,
  output [6:0] HEX0, HEX1 /*, HEX2, HEX3*/,

  input UART_RXD,
  output UART_TXD
);

  reg [7:0] data = 0;
  wire ready;

  assign LEDR[0] = ready;

  segment_display hex0(
    .num(data[3:0]),
    .hex(HEX0)
  );
  segment_display hex1(
    .num(data[7:4]),
    .hex(HEX1)
  );

  uart_rx rx(
    .clk(CLOCK_50),
    .rx(UART_RXD),
    .ready(ready),
    .data(data)
  );
  uart_tx tx(
    .clk(CLOCK_50),
    .tx(UART_TXD),
    .write(ready),
    .data(data),
    .busy(LEDR[1])
  );
  
endmodule
