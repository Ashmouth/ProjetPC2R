OCAMLC=ocamlc
OCAMLOPT=ocamlopt
OCAMLDEP=ocamldep
INCLUDES=                 # all relevant -I options here
OCAMLFLAGS=-thread unix.cma threads.cma $(INCLUDES)     # add other options for ocamlc here
OCAMLOPTFLAGS=-thread unix.cmxa threads.cmxa $(INCLUDES) # add other options for ocamlopt here

# The list of object files for prog2
PROG_OBJS=settings.cmo reservoir.cmo dict.cmo plateau.cmo rwl.cmo server.cmo
PROG_OBJS_X=settings.cmx reservoir.cmx dict.cmx plateau.cmx rwl.cmx server.cmx

serv.exe: $(PROG_OBJS) $(PROG_OBJS_X)
	$(OCAMLC) -o serv.exe $(OCAMLFLAGS) $(PROG_OBJS)
	$(OCAMLOPT) -o serv.exe.opt $(OCAMLOPTFLAGS) $(PROG_OBJS_X)

# Common rules
.SUFFIXES: .ml .mli .cmo .cmi .cmx

.ml.cmo:
	$(OCAMLC) $(OCAMLFLAGS) -c $<

.mli.cmi:
	$(OCAMLC) $(OCAMLFLAGS) -c $<

.ml.cmx:
	$(OCAMLOPT) $(OCAMLOPTFLAGS) -c $<

# Clean up
clean:
	rm -f serv.exe serv.exe.opt
	rm -f *.cm[iox] *.o

# Dependencies
.depend:
	$(OCAMLDEP) $(INCLUDES) *.mli *.ml > .depend

include .depend