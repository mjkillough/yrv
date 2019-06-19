module yrv_top(
	input  [17:0] SW,
	output [17:0] LEDR,
	output [6:0] HEX0, HEX1
	);

    wire clk;
    wire res;

	Yrv yrv(.clock(clk), .reset(res), .io_hex0(HEX1), .io_sw(SW[4:0]));
endmodule
