objects = user.o alloc.o
all:main server client

main: user.o main.o alloc.o
	g++ user.o main.o alloc.o -o main
main.o: main.cpp
	g++ -c main.cpp -o main.o --std=c++11

server: server.o user.o alloc.o
	g++ server.o user.o alloc.o -o server -lboost_system -lboost_thread -lpthread

server.o: server.cpp user.o
	g++ -c server.cpp -o server.o --std=c++11

client: client.o user.o alloc.o
	g++ client.o user.o alloc.o -o client -lboost_system -lboost_thread -lpthread

client.o: client.cpp user.o
	g++ -c client.cpp -o client.o --std=c++11


user: user.o alloc.o
	g++ alloc.o user.o -o usertest

alloc.o: alloc.h exceptions.h
	g++ -c alloc.cpp -o alloc.o --std=c++11

user.o : alloc.h alloc.cpp bptree.hpp exceptions.h String.hpp user.hpp
	g++ -c user.cpp -o user.o --std=c++11

.PHONY : clean
clean:
	rm main server client *.o 
