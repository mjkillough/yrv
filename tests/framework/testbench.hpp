#include <memory>

#include <verilated_vcd_c.h>

template<class T>
class Testbench {
public:
    std::unique_ptr<T> module;

    Testbench() {
        module = std::make_unique<T>();
        Verilated::traceEverOn(true);
    }

    void trace(std::string name) {
        vcd = std::make_unique<VerilatedVcdC>();
        module->trace(vcd.get(), 99);
        vcd->open(name.c_str());
    }

    void tick(size_t num = 1) {
        tick_while(num, [](){});
    }

    template<typename F>
    void tick_while(size_t num, F assertion) {
        for (size_t i = 0; i < num; i++) {
            INFO("tick(" << +i << +")");
            tick_count++;

            // Allow all combinatorial logic to settle before clocking.
            module->clk = 0;
            module->eval();
            dump(10 * tick_count - 2);

            module->clk = 1;
            module->eval();
            dump(10 * tick_count);
            
            tick_count++;
            module->clk = 0;
            module->eval();
            dump(10 * tick_count + 5);

            assertion();
        }
    }

    void reset() {
        // Reset on negative edge of resetn:
        module->resetn = 1;
        module->eval();
        module->resetn = 0;
        module->eval();
        dump(10 * tick_count - 3);

        module->resetn = 1;
    }

    virtual void finish() {
        if (vcd) {
            vcd->close();
        }
    }

protected:
    std::unique_ptr<VerilatedVcdC> vcd;
    uint64_t tick_count{0};

    void dump(uint64_t time) {
        if (vcd) {
            vcd->dump(tick_count);
        }
    }
};
