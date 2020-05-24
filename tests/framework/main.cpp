#define CATCH_CONFIG_RUNNER
#include "catch.hpp"

#include <verilated.h>

int main(int argc, char **argv) {
	Verilated::commandArgs(argc, argv);
    int result = Catch::Session().run(argc, argv);
    return result;
}
