#include <stdlib.h>
#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Vuart_tx.h"

TEST_CASE("uart_tx") {
	Testbench<Vuart_tx> tb;

    // This is the default given CLOCK_HZ=10, BAUD_RATE=1:
    auto clocks_per_baud = 10;

    auto assert_not_busy = [&]() { REQUIRE(tb.module->busy == 0); };
    auto assert_busy = [&]() { REQUIRE(tb.module->busy == 1); };
    auto assert_start_bit = [&]() { REQUIRE(tb.module->tx == 0); };
    auto assert_stop_bit = [&]() { REQUIRE(tb.module->tx == 1); };

    auto transmit = [&](auto byte) {
        tb.module->write = 1;
        tb.module->data = byte;
        tb.tick_while(1, assert_busy);

        INFO("start bit");
        tb.tick_while(clocks_per_baud, [&]() {
            assert_busy();
            assert_start_bit();
        });

        tb.module->write = 0;

        for (size_t bit = 0; bit < 8; bit++) {
            INFO("waiting for bit " << +bit);

            tb.tick_while(clocks_per_baud, [&]() {
                assert_busy();
                REQUIRE(tb.module->tx == (byte & 1 << bit) >> bit);
            });
        }

        INFO("stop bit");
        tb.tick_while(clocks_per_baud, [&]() {
            assert_busy();
            assert_stop_bit();
        });
    };

    // Hold write low an arbitrary amount of time.
    // Wait for a random amount of time before and after.
    auto random_cycles = GENERATE(0, take(1, random(0, 50)));
    INFO("random " << +random_cycles << " cycles");

    tb.module->write = 0;
    tb.reset();
    tb.tick_while(random_cycles, assert_not_busy);

    SECTION("one byte") {
        transmit(105);
    }

    SECTION("multiple bytes") {
        for (auto i = 0; i < 256; i++) {
            INFO("transmitting " << +i << "th byte");
            transmit(i);

            INFO("random amount after")
            tb.tick_while(random_cycles, assert_not_busy);
        }
    }

    tb.finish();
}
