# test:
# 	@sbt 'testOnly'

# compile:
# 	@sbt 'runMain yrv.Yrv --target-dir verilog/generated'

assemble: 
	@quartus_map quartus/yrv
	@quartus_fit quartus/yrv
	@quartus_asm quartus/yrv

program: assemble
	@quartus_pgm -z --mode=JTAG --operation="p;quartus/output_files/yrv.sof"

# verilator -Wall --trace -CFLAGS -std=c++17 --cc src/uart_receiver.sv --exe testbench.cc 
# cd obj_dir/
# make -f Vuart_receiver.mk
# ./Vuart_receiver
# cd ..

CFLAGS = -std=c++17

BUILD = build/
BIN = bin/
SRC = src/
TEST_SRC = tests/
TEST_TARGET = $(BIN)test

SRCS := $(wildcard $(SRC)*.sv) $(wildcard $(SRC)**/*.sv)

# Compile VERILATOR_SRCS. These are two cpp files shipped with Verilator that
# must be compiled and linked into the simulation.

VERILATOR_INCLUDE = /usr/share/verilator/include/
VERILATOR_RAW_SRCS = verilated.cpp verilated_vcd_c.cpp
VERILATOR_SRCS := $(addprefix $(VERILATOR_INCLUDE)/,$(VERILATOR_RAW_SRCS))
VERILATOR_OBJECTS := $(addprefix $(BUILD),$(subst .cpp,.o,$(VERILATOR_RAW_SRCS)))

$(BUILD)%.o: $(VERILATOR_INCLUDE)%.cpp
	@mkdir -p $(BUILD)$(<D)
	@$(CXX) $(CFLAGS) -c $< -o $@

# Verilate each .sv separately and compile to a static library.

VERILATED_MK := $(patsubst $(SRC)%.sv,$(BUILD)verilated/V%.mk,$(SRCS))
VERILATED_LIBS := $(patsubst $(SRC)%.sv,$(BUILD)verilated/V%_ALL.a,$(SRCS))
VERILATED_LIB_FLAGS := $(patsubst $(SRC)%.sv,-l:V%__ALL.a,$(SRCS))

verilate: $(VERILATED_LIBS) $(VERILATED_MK)

$(BUILD)verilated/V%.mk: $(SRC)%.sv $(BUILD)
	@verilator -Wall -Wno-VARHIDDEN --trace --Mdir $(BUILD)verilated -I$(SRC) --cc $< 

$(BUILD)verilated/V%_ALL.a: $(BUILD)verilated/V%.mk
	@$(MAKE) -s -C $(BUILD)verilated -f $(<F)

# Compile all of our CPP test files and link them with verilated code and
# VERILATOR_SRC.

TEST_SRCS := $(wildcard $(TEST_SRC)*.cpp) $(wildcard $(TEST_SRC)**/*.cpp)
TEST_OBJECTS := $(patsubst $(TEST_SRC)%.cpp,$(BUILD)tests/%.o,$(TEST_SRCS))
TEST_DEPS := $(TEST_OBJECTS:.o=.d)

-include $(TEST_DEPS)

test: $(TEST_TARGET)
	@$(TEST_TARGET) $(PATTERN)

$(TEST_TARGET): $(VERILATED_LIBS) $(TEST_OBJECTS) $(VERILATOR_OBJECTS) $(BIN)
	@$(CXX) -o $(TEST_TARGET) $(TEST_OBJECTS) $(VERILATOR_OBJECTS) $(VERILATED_LIB_FLAGS) -L$(BUILD)verilated

$(BUILD)tests/%.o: $(TEST_SRC)%.cpp $(BUILD)
	@mkdir -p $(BUILD)$(<D)
	@$(CXX) $(CFLAGS) -I$(TEST_SRC) -I$(BUILD)verilated -I/usr/share/verilator/include -MMD -c $< -o $@

$(BUILD):
	@mkdir -p $@tests
	@mkdir -p $@verilated

$(BIN):
	@mkdir -p $@

clean:
	@-rm -rf project/project/
	@-rm -rf project/target/
	@-rm -rf target/
	@-rm -rf test_run_dir/
	@-rm -rf quartus/*.qpf
	@-rm -rf quartus/db/
	@-rm -rf quartus/incremental_db/
	@-rm -rf quartus/output_files/
	@-rm -rf $(BUILD)
	@-rm -rf $(BIN)

.PHONY: verilate test clean
