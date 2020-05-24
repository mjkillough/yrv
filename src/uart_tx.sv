module uart_tx#(
  parameter CLOCK_HZ = 10,
  parameter BAUD_RATE = 1
)(
  input resetn,
  input clk,
  input write,
  input [7:0] data,

  output reg busy,
  output reg tx
);

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

  reg [2:0] bit_count = 0;
  // Buffers data when write held high.
  reg [7:0] buffer;

  // Reset baud counter on reset and when we start to write.
  wire tick, baud_resetn;
  assign baud_resetn = resetn && !(state == STATE_IDLE && write);
  uart_baud#(
    .CLOCK_HZ(CLOCK_HZ),
    .BAUD_RATE(BAUD_RATE)
  ) baud(
    .resetn(baud_resetn),
    .clk(clk),
    .half(0),
    .tick(tick)
  );

  always @(posedge clk, negedge resetn) begin
    if (!resetn) state <= STATE_IDLE;
    else case (state)
      STATE_IDLE: begin
        tx <= 1'b1;

        if (write) begin
          busy <= 1'b1;
          buffer <= data;
          bit_count <= 0;
          state <= STATE_START;
        end else
          busy <= 1'b0;
      end

      STATE_START: begin
        tx <= 1'b0;

        if (tick)
          state <= STATE_DATA;
      end

      STATE_DATA: begin
        tx <= buffer[bit_count];

        if (tick) begin
          if (bit_count < 7)
            bit_count <= bit_count + 1'b1;
          else
            state <= STATE_STOP;
        end
      end

      STATE_STOP: begin
        tx <= 1'b1;

        if (tick)
          state <= STATE_IDLE;
      end
    endcase
  end

endmodule
