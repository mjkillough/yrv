package yrv

import chisel3._
import chisel3.util._

class Pulse extends Module {
    val io = IO(new Bundle {
        val in = Input(Bool())
        val out = Output(Bool())
    })

    val r1 = RegNext(io.in)
    val r2 = RegNext(r1)
    val r3 = RegNext(r2)

    io.out := r2 && !r3
}

object Pulse {
    def apply(in: Bool) = {
        val pulse = Module(new Pulse)
        pulse.io.in := in
        pulse.io.out
    }
}
