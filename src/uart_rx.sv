module uart_rx#(
  parameter CLOCK_HZ = 10,
  parameter BAUD_RATE = 1
)(
  input resetn,
  input clk,
  input rx,

  output reg ready,
  output reg [7:0] data
);

  enum reg [1:0] {
    // Waiting for start bit.
    STATE_IDLE,
    // Finding middle of start bit.
    STATE_START,
    // Finding middle of each bit of data.
    STATE_DATA,
    // Waits for middle of stop bit.
    // Pull ready high for one clock to indicate data available. .
    STATE_STOP
  } state;

  // The bit that's currently being received.
  reg [2:0] bit_count = 0;

  // Wait for start bit of 0, which can happen at any time. Wait for another
  // CLOCKS_PER_BAUD / 2 clocks after that to get to the middle of the start
  // bit. Then wait CLOCKS_PER_BAUD clocks for each bit to be transmitted,
  // which will ensure we read the middle of each bit. Finally wait for the
  // stop bit to be received.

  wire tick, half;
  assign half = (state == STATE_IDLE && rx == 1'b0);
  uart_baud#(
    .CLOCK_HZ(CLOCK_HZ),
    .BAUD_RATE(BAUD_RATE)
  ) baud(
    .resetn(resetn),
    .clk(clk),
    .half(half),
    .tick(tick)
  );

  always_ff @(posedge clk, negedge resetn) begin
    if (!resetn) state <= STATE_IDLE;
    else case (state)
      STATE_IDLE: begin
        ready <= 1'b0;

        if (rx == 1'b0)
          state <= STATE_START;
      end

      STATE_START: begin
        if (tick) begin
          // If the start bit is still 0, start counting for data.
          // Otherwise, give up.
          if (rx == 1'b0)
            state <= STATE_DATA;
          else
            state <= STATE_IDLE;
        end
      end

      STATE_DATA: begin
        if (tick) begin
          data[bit_count] <= rx;

          if (bit_count < 7)
            bit_count <= bit_count + 1'b1;
          else
            state <= STATE_STOP;
        end
      end

      STATE_STOP: begin
        if (tick) begin
          bit_count <= 0;
          ready <= 1'b1;
          state <= STATE_IDLE;
        end
      end
    endcase
  end

endmodule
