module yrv_top(
	input CLOCK_50,
	input  [17:0] SW,
	input  [3:0] KEY,
	output [17:0] LEDR,
	output [6:0] HEX0, HEX1,

	input UART_RXD,
	output UART_TXD
	);

    wire res;

	Yrv yrv(
		.clock(CLOCK_50),
		.reset(!KEY[0]),

		.io_hex0(HEX0),
		.io_hex1(HEX1),

		.io_uart_tx(UART_TXD),
		.io_uart_rx(UART_RXD),
		.io_led(LEDR[0]),
		.io_button(!KEY[1]),
		.io_data(SW[7:0])
	);
endmodule
