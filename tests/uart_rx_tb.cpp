#include <stdlib.h>
#include <cstdint>
#include <string>
#include <iostream>
#include <optional>

#include "framework/catch.hpp"
#include "framework/testbench.hpp"

#include "Vuart_rx.h"

class UartRxTb: public Testbench<Vuart_rx> {};

TEST_CASE("uart_rx") {
	UartRxTb tb;
    tb.trace("test.vcd");

    auto assert_not_ready = [&]() { REQUIRE(tb.module->ready == 0); };

    auto transmit = [&](uint8_t byte) {
        INFO("transmit(" << +byte << ", start bit)");
        tb.module->rx = 0;
        tb.tick_while(434, assert_not_ready);

        for (uint8_t bit = 0; bit < 8; bit++) {
            INFO("transmit(" << +byte << ", bit " << +bit <<")");

            tb.module->rx = (byte & (1 << bit)) >> bit;
            tb.tick_while(433, assert_not_ready);
        }

        INFO("transmit(" << +byte << ", stop bit)");
        tb.module->rx = 1;
        // Should be ready for one cycle during the stop bit, with the data
        // available for reading.
        auto ready = false;
        auto data = std::optional<uint8_t>{};
        for (size_t i = 0; i < 434; i++) {
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
    auto random_high = GENERATE(0, take(1, random(0, 200 * 434)));
    INFO("holding high " << +random_high << " cycles");
    tb.module->rx = 1;
    tb.tick_while(1000, assert_not_ready);

    SECTION("one byte") {
        transmit(105);
    }

    SECTION("multiple bytes") {
        for (auto i = 0; i < 256; i++) {
            INFO("transmitting " << +i << "th byte");
            transmit(static_cast<uint8_t>(i));
        }
    }

    tb.finish();
}
