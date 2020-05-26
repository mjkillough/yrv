#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Vregfile.h"

TEST_CASE("regfile") {
    Testbench<Vregfile> tb;

    SECTION("zero register") {
        INFO("read");
        tb.module->rs1_addr = 0;
        tb.module->rs2_addr = 0;
        tb.tick();

        REQUIRE(tb.module->rs1_data == 0);
        REQUIRE(tb.module->rs2_data == 0);

        INFO("write");
        tb.module->rd_write = 1;
        tb.module->rd_addr = 0;
        tb.module->rd_data = 1;
        tb.tick();
        tb.module->rd_write = 0;

        INFO("read again");
        tb.tick();
        REQUIRE(tb.module->rs1_data == 0);
        REQUIRE(tb.module->rs2_data == 0);
    }

    SECTION("other registers") {
        for (auto i = 1; i < 32; i++) {
            auto data = GENERATE(take(1, random(0, 256)));

            INFO("write r" << +i << "=" << data);
            tb.module->rd_write = 1;
            tb.module->rd_addr = i;
            tb.module->rd_data = data;
            tb.tick();
            tb.module->rd_write = 0;

            INFO("read r" << +i);
            tb.module->rs1_addr = i;
            tb.module->rs2_addr = i;
            tb.tick();

            REQUIRE(tb.module->rs1_data == data);
            REQUIRE(tb.module->rs2_data == data);
        }
    }

    SECTION("read two registers") {
        auto data1 = GENERATE(take(1, random(0, 256)));
        auto data2 = GENERATE(take(1, random(0, 256)));

        INFO("write r1=" << data1);
        tb.module->rd_write = 1;
        tb.module->rd_addr = 1;
        tb.module->rd_data = data1;
        tb.tick();
        INFO("write r2=" << data2);
        tb.module->rd_addr = 2;
        tb.module->rd_data = data2;
        tb.tick();
        tb.module->rd_write = 0;

        INFO("read r1, r2");
        tb.module->rs1_addr = 1;
        tb.module->rs2_addr = 2;
        tb.tick();

        REQUIRE(tb.module->rs1_data == data1);
        REQUIRE(tb.module->rs2_data == data2);
    }

    tb.finish();
}

