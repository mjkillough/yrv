
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
        (opcode & 0x7F);
}

static
uint32_t instr_r(uint8_t funct7, uint8_t rs2, uint8_t rs1, uint8_t func3, uint8_t rd, uint8_t opcode) {
    return ((funct7 & 0x3F) << 25) |
        ((rs2 & 0x1F) << 20) |
        ((rs1 & 0x1F) << 15) |
        ((func3 & 0x3) << 12) |
        ((rd & 0x1F) << 7) |
        (opcode & 0x7F);
}

static
uint32_t instr_b(uint16_t imm, uint8_t rs2, uint8_t rs1, uint8_t func3, uint8_t opcode) {
    // 13 bit immediate is split into 4, ignoring lower bit.
    // 1 0000 0000 0000 = 0x1000
    // 0 0111 1110 0000 = 0x03E0
    // 0 0000 0001 1110 = 0x001E
    // 0 1000 0000 0000 = 0x0800
    auto imm1 = (imm & 0x1000) >> 12;
    auto imm2 = (imm & 0x03E0) >> 5;
    auto imm3 = (imm & 0x001E) >> 1;
    auto imm4 = (imm & 0x0800) >> 11;

    return (imm1 << 31) |
        (imm2 << 25) |
        ((rs2 & 0x1F) << 20) |
        ((rs1 & 0x1F) << 15) |
        ((func3 & 0x3) << 12) |
        (imm3 << 8) |
        (imm4 << 7) |
        (opcode & 0x7F);
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

    SECTION("bne") {
        // bne x1, x2, 10

        tb.module->instr = instr_b(10, 2, 1, 0b001, 0b1100011);
        regs[1] = 1;
        regs[2] = 2;

        tb.tick();

        REQUIRE(tb.module->cpu__DOT__pc == 10);
    }

    tb.finish();
}

