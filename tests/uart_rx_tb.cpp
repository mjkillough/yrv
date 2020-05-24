#include <stdlib.h>
#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Vuart_rx.h"

TEST_CASE("uart_rx") {
	Testbench<Vuart_rx> tb;

    // This is the default given CLOCK_HZ=10, BAUD_RATE=1:
    auto clocks_per_baud = 10;

    auto assert_not_ready = [&]() { REQUIRE(tb.module->ready == 0); };

    auto receive = [&](auto byte) {
        INFO("receive(" << +byte << ", start bit)");
        tb.module->rx = 0;
        tb.tick_while(clocks_per_baud, assert_not_ready);

        for (uint8_t bit = 0; bit < 8; bit++) {
            INFO("receive(" << +byte << ", bit " << +bit <<")");

            tb.module->rx = (byte & (1 << bit)) >> bit;
            tb.tick_while(clocks_per_baud, assert_not_ready);
        }
        tb.module->rx = 1;

        INFO("receive(" << +byte << ", stop bit)");
        // Should be ready for one cycle during the stop bit, with the data
        // available for reading.
        auto ready = false;
        auto data = std::optional<uint8_t>{};
        for (size_t i = 0; i < clocks_per_baud; i++) {
            if (tb.module->ready == 1) {
                REQUIRE(!ready);
                ready = true;
                data.emplace(tb.module->data);
            }
            tb.tick();
        }
        REQUIRE(ready);
        REQUIRE(data);
        REQUIRE(data.value() == byte);
    };

    // Hold rx high an arbitrary amount of time.
    auto random_high = GENERATE(0, take(1, random(0, 50)));
    INFO("holding high " << +random_high << " cycles");
    tb.module->rx = 1;
    tb.reset();
    tb.tick_while(random_high, assert_not_ready);

    SECTION("one byte") {
        receive(105);
    }

    SECTION("multiple bytes") {
        for (auto i = 0; i < 256; i++) {
            INFO("receiving " << +i << "th byte");
            receive(i);
        }
    }

    tb.finish();
}
