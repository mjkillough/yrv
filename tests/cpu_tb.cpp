
#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Vcpu.h"

static
uint32_t instr_i(uint16_t imm, uint8_t rs1, uint8_t func3, uint8_t rd, uint8_t opcode) {
    return ((imm & 0xFFF) << 20) |
        ((rs1 & 0x1F) << 15) |
        ((func3 & 0x3) << 12) |
        ((rd & 0x1F) << 7) |
        (opcode & 0x3F);
}

static
uint32_t instr_r(uint8_t funct7, uint8_t rs2, uint8_t rs1, uint8_t func3, uint8_t rd, uint8_t opcode) {
    return ((funct7 & 0x3F) << 25) |
        ((rs2 & 0x1F) << 20) |
        ((rs1 & 0x1F) << 15) |
        ((func3 & 0x3) << 12) |
        ((rd & 0x1F) << 7) |
        (opcode & 0x3F);
}

TEST_CASE("cpu") {
    Testbench<Vcpu> tb;

    auto regs = tb.module->cpu__DOT__decode__DOT__regs__DOT__regs;

    SECTION("addi") {
        // addi x1, x1, 10
        tb.module->instr = instr_i(10, 1, 0, 1, 0b0010011);

        tb.tick();
        REQUIRE(regs[1] == 10);
        tb.tick();
        REQUIRE(regs[1] == 20);
        tb.tick();
        REQUIRE(regs[1] == 30);
        tb.tick();
        REQUIRE(regs[1] == 40);
    }

    SECTION("sub") {
      // sub x1, x2, x3
      // x1 = x2 - x3

      tb.module->instr = instr_r(0b0100000, 3, 2, 0b000, 1, 0b0110011);
      regs[1] = 100;
      regs[2] = 95;
      regs[3] = 6;

      tb.tick();

      REQUIRE(regs[1] == 89);
      REQUIRE(regs[2] == 95);
      REQUIRE(regs[3] == 6);
    }

    tb.finish();
}

