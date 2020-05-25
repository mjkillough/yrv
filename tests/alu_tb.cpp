#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Valu.h"
#include "Valu___024unit.h"


TEST_CASE("alu") {
    Testbench<Valu> tb;

    using alu_func = Valu___024unit;

    auto data1 = GENERATE(take(10, random(-256, 256)));
    auto data2 = GENERATE(take(10, random(-256, 256)));
    INFO("data1=" << data1 << " data2=" << data2);

    tb.module->in1 = data1;
    tb.module->in2 = data2;

    SECTION("add") {
        tb.module->func = alu_func::ALU_FUNC_ADD;
        tb.eval();
        REQUIRE(tb.module->out == (data1 + data2));
    }

    SECTION("sub") {
        tb.module->func = alu_func::ALU_FUNC_SUB;
        tb.eval();
        REQUIRE(tb.module->out == (data1 - data2));
    }

    SECTION("and") {
        tb.module->func = alu_func::ALU_FUNC_AND;
        tb.eval();
        REQUIRE(tb.module->out == (data1 & data2));
    }

    SECTION("or") {
        tb.module->func = alu_func::ALU_FUNC_OR;
        tb.eval();
        REQUIRE(tb.module->out == (data1 | data2));
    }

    SECTION("xor") {
        tb.module->func = alu_func::ALU_FUNC_XOR;
        tb.eval();
        REQUIRE(tb.module->out == (data1 ^ data2));
    }

    SECTION("sll") {
        tb.module->func = alu_func::ALU_FUNC_SLL;
        tb.eval();
        REQUIRE(tb.module->out == (static_cast<uint64_t>(data1) << (data2 & 0x1F)));
    }

    SECTION("srl") {
        tb.module->func = alu_func::ALU_FUNC_SRL;
        tb.eval();
        REQUIRE(tb.module->out == (static_cast<uint64_t>(data1) >> (data2 & 0x1F)));
    }

    SECTION("sra") {
        tb.module->func = alu_func::ALU_FUNC_SRA;
        tb.eval();
        REQUIRE(tb.module->out == (data1 >> (data2 & 0x1F)));
    }

    SECTION("seq") {
        tb.module->func = alu_func::ALU_FUNC_SEQ;
        tb.eval();
        REQUIRE(tb.module->out == (data1 == data2));
    }

    SECTION("slt") {
        tb.module->func = alu_func::ALU_FUNC_SLT;
        tb.eval();
        REQUIRE(tb.module->out == (data1 < data2));
    }

    SECTION("sltu") {
        tb.module->func = alu_func::ALU_FUNC_SLTU;
        tb.eval();
        REQUIRE(tb.module->out == (static_cast<unsigned int>(data1) < static_cast<unsigned int>(data2)));
    }

    tb.finish();
}

