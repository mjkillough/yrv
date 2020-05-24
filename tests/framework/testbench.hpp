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
            tick_count++;

            INFO("tick(" << +i << +")");

            module->clk = 0;
            module->eval();
            dump();
            assertion();
            module->clk = 1;
            module->eval();
            dump();
            assertion();
        }
    }

    virtual void finish() {
        vcd->close();
    }

protected:
    std::unique_ptr<VerilatedVcdC> vcd;
    uint64_t tick_count{0};

    void dump() {
        if (vcd) {
            vcd->dump(tick_count);
        }
    }
};
