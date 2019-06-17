test:
	@sbt 'testOnly'

program:
	@quartus_map quartus/yrv
	@quartus_fit quartus/yrv
	@quartus_asm quartus/yrv
	@quartus_pgm -z --mode=JTAG --operation="p;quartus/yrv/output_files/yrv.sof"

clean:
	@-rm -rf project/project/
	@-rm -rf project/target/
	@-rm -rf target/
	@-rm -rf test_run_dir/
	@-rm -rf quartus/*.qpf
	@-rm -rf quartus/db/
	@-rm -rf quartus/incremental_db/
	@-rm -rf quartus/output_files/
