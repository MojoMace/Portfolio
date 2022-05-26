#make file

all: RunP6 main.o 
main.o: main.c
	gcc -c main.c
RunP6: main.o
	gcc -Wall main.o foo.h -o RunP6  
clean:
	rm main.o RunP6
