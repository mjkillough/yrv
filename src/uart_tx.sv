module uart_tx(
  input clk,
  input write,
  input [7:0] data,

  output reg busy,
  output reg tx
);

  // Clock Rate Hz / Baud Rate
  // 50 MHz / 115200 = 434
  parameter CLOCKS_PER_BAUD = 434;

  enum reg [1:0] {
    // Waiting for write to go high.
    STATE_IDLE,
    // Transmitting start bit.
    STATE_START,
    // Transmitting each bit of data.
    STATE_DATA,
    // Transmit stop bit.
    STATE_STOP
  } state;

  reg [9:0] clock_count = 0;
  reg [2:0] bit_count = 0;
  // Buffers data when write held high.
  reg [7:0] buffer;

  always @(posedge clk) begin
    case (state)
      STATE_IDLE: begin
        if (write) begin
          clock_count <= CLOCKS_PER_BAUD;
          buffer <= data;
          busy <= 1'b1;
          state <= STATE_START;
        end
      end

      STATE_START: begin
        tx <= 1'b0;

        if (clock_count > 0) begin
          clock_count <= clock_count - 1;
        end else begin
          clock_count <= CLOCKS_PER_BAUD;
          state <= STATE_DATA;
        end
      end

      STATE_DATA: begin
        tx <= buffer[bit_count];

        if (clock_count > 0) begin
          clock_count <= clock_count - 1;
        end else begin
          clock_count <= CLOCKS_PER_BAUD;

          if (bit_count < 7) begin
            bit_count <= bit_count + 1;
          end else begin
            bit_count <= 0;
            state <= STATE_STOP;
          end
        end
      end

      STATE_STOP: begin
        tx <= 1'b1;

        if (clock_count > 0) begin
          clock_count <= clock_count - 1;
        end else begin
          state <= STATE_IDLE;
          busy <= 1'b0;
        end
      end
    endcase
  end

endmodule
