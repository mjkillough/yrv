#include <stdlib.h>
#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Vuart_baud.h"

TEST_CASE("uart_baud") {
	Testbench<Vuart_baud> tb;

    tb.reset();

    // This is the default given CLOCK_HZ=10, BAUD_RATE=1:
    auto clocks_per_baud = 10;

    SECTION("cycling through") {
        for (auto i = 0; i < 3; i++) {
            INFO("cycle " << i);

            auto clock_count = 0;
            while (tb.module->tick == 0) {
                tb.tick();
                clock_count++;
            }
            REQUIRE(clock_count == clocks_per_baud - 1);

            REQUIRE(tb.module->tick == 1);
            tb.tick();
        }
    }

    SECTION("half cycle") {
        tb.module->half = 1;
        tb.tick();
        tb.module->half = 0;

        auto clock_count = 0;
        while (tb.module->tick == 0) {
            tb.tick();
            clock_count++;
        }
        REQUIRE(clock_count == (clocks_per_baud / 2)- 1);

        REQUIRE(tb.module->tick == 1);
        tb.tick();
    }
    
    tb.finish();
}
