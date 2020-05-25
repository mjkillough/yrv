#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Vimmediate_decoder.h"

uint32_t instr_i(uint16_t imm, uint8_t rs1, uint8_t func3, uint8_t rd, uint8_t opcode) {
    return ((imm & 0xFFF) << 20) |
        ((rs1 & 0x1F) << 15) |
        ((func3 & 0x3) << 12) |
        ((rd & 0x1F) << 7) |
        (opcode & 0x3F);
}

template<class T>
T sign_extend(T x, const int bits) {
    T m = 1;
    m <<= bits - 1;
    return (x ^ m) - m;
}

TEST_CASE("immediate_decoder") {
    Testbench<Vimmediate_decoder> tb;

    SECTION("i-type") {
        auto rs1 = GENERATE(take(1, random(0, 32)));
        auto rd = GENERATE(take(1, random(0, 32)));
        auto func3 = GENERATE(take(1, random(0, 4)));
        // OP-IMM and OP-IMM-32
        auto opcode = GENERATE(0b0010011, 0b0011011);

        INFO("rs1=" << rs1 << " rd=" << rd << " func3=" << func3 << " opcode=" << opcode);

        SECTION("positive") {
            auto imm = 0b010101010101;
            INFO("imm=" << imm);

            tb.module->instr = instr_i(imm, rs1, func3, rd, opcode);
            tb.eval();
            REQUIRE(tb.module->imm == imm);
        }

        SECTION("negative") {
            auto imm = 0b110101010101;
            INFO("imm=" << imm);

            tb.module->instr = instr_i(imm, rs1, func3, rd, opcode);
            tb.eval();
            REQUIRE(tb.module->imm == sign_extend(imm, 12));
        }
    }

    tb.finish();
}

