
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

TEST_CASE("cpu") {
    Testbench<Vcpu> tb;

    // addi x1, x1, 10
    tb.module->instr = instr_i(10, 1, 0, 1, 0b0010011);

    tb.tick();
    REQUIRE(tb.module->cpu__DOT__rd_data == 10);
    tb.tick();
    REQUIRE(tb.module->cpu__DOT__rd_data == 20);
    tb.tick();
    REQUIRE(tb.module->cpu__DOT__rd_data == 30);
    tb.tick();
    REQUIRE(tb.module->cpu__DOT__rd_data == 40);

    tb.finish();
}

