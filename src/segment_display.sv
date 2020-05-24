module segment_display(
  input [3:0] num,
  output logic [6:0] hex
);
  always_comb
    case (num)
      4'd0:    hex = ~7'b0111111;
      4'd1:    hex = ~7'b0000110;
      4'd2:    hex = ~7'b1011011;
      4'd3:    hex = ~7'b1001111;
      4'd4:    hex = ~7'b1100110;
      4'd5:    hex = ~7'b1101101;
      4'd6:    hex = ~7'b1111101;
      4'd7:    hex = ~7'b0000111;
      4'd8:    hex = ~7'b1111111;
      4'd9:    hex = ~7'b1101111;
      4'd10:   hex = ~7'b1110111;
      4'd11:   hex = ~7'b1111100;
      4'd12:   hex = ~7'b0111001;
      4'd13:   hex = ~7'b1011110;
      4'd14:   hex = ~7'b1111001;
      4'd15:   hex = ~7'b1110001;
      default: hex = ~7'b0000000;
    endcase
endmodule