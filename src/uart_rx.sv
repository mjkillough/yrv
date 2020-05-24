module uart_rx(
  input clk,
  input rx,

  output reg ready,
  output reg [7:0] data
);

  // Clock Rate Hz / Baud Rate
  // 50 MHz / 115200 = 434
  parameter CLOCKS_PER_BAUD = 10'd434;

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

  // Count down from CLOCKS_PER_BAUD, reading value whenever this is 0.
  reg [9:0] clock_count = 0;
  // The bit that's currently being received.
  reg [2:0] bit_count = 0;

  // Wait for start bit of 0, which can happen at any time. Wait for another
  // CLOCKS_PER_BAUD / 2 clocks after that to get to the middle of the start
  // bit. Then wait CLOCKS_PER_BAUD clocks for each bit to be transmitted,
  // which will ensure we read the middle of each bit. Finally wait for the
  // stop bit to be received.

  always_ff @(posedge clk) begin
    case (state)
      STATE_IDLE: begin
        ready <= 1'b0;
        clock_count <= CLOCKS_PER_BAUD / 10'd2;
        if (rx == 1'b0) state <= STATE_START;
      end

      STATE_START: begin
        if (clock_count > 0) begin
          clock_count <= clock_count - 1'b1;
        end else begin
          // If the start bit is still 0, start counting for data.
          // Otherwise, give up.
          if (rx == 1'b0) begin
            state <= STATE_DATA;
            clock_count <= CLOCKS_PER_BAUD;
          end else begin
            state <= STATE_IDLE;
          end
        end
      end

      STATE_DATA: begin
        if (clock_count > 0) begin
          clock_count <= clock_count - 1'b1;
        end else begin
          data[bit_count] <= rx;
          clock_count <= CLOCKS_PER_BAUD;

          if (bit_count < 7) begin
            bit_count <= bit_count + 1'b1;
          end else begin
            bit_count <= 0;
            state <= STATE_STOP;
          end
        end
      end

      STATE_STOP: begin
        if (clock_count > 0) begin
          clock_count <= clock_count - 1'b1;
        end else begin
          ready <= 1'b1;
          state <= STATE_IDLE;
        end
      end
    endcase
  end

endmodule
