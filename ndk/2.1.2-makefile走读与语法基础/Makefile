main: main.o tool.o;

	gcc main.o tool.o -o main

main.o: main.c
	gcc -c main.c -o main.o
tool.o: tool.c
	gcc -c tool.c -o tool.o
define FUNC
$(info echo "hello")
endef
$(call FUNC)

define FUNC1
$(info echo $(1) $(2))
endef
$(call FUNC1,hello,wolrd)


.PHONY: clean

clean:

	-rm main *.o