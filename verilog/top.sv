module yrv_top(
	input CLOCK_50,
	input  [17:0] SW,
	input  [3:0] KEY,
	output [17:0] LEDR,
	output [6:0] HEX0, HEX1,
	output UART_TXD
	);

    wire res;

	Yrv yrv(
		.clock(CLOCK_50),
		.reset(SW[13]),
		.io_hex0(HEX1),
		.io_sw(SW[3:0]),
		.io_sw2(SW[11:4]),
		.io_sw3(!KEY[0]),
		.io_led(LEDR[0]),
		.io_uart_tx(UART_TXD)
	);
endmodule
