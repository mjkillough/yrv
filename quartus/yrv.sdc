set_time_format -unit ns -decimal_places 3

create_clock -period "50 MHz"  -name clock_50 [get_ports CLOCK_50]

derive_pll_clocks -create_base_clocks
derive_clock_uncertainty

set_output_delay -clock clock_50 -min 0 [get_ports {HEX* LEDR* UART_TXD}]
set_output_delay -clock clock_50 -max 0 [get_ports {HEX* LEDR* UART_TXD}]

set_input_delay -clock clock_50 -min 0 [get_ports {SW* UART_RXD}]
set_input_delay -clock clock_50 -max 0 [get_ports {SW* UART_RXD}]
