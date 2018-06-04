objects = user.o alloc.o
data_structures = date.hpp bptree.hpp String.hpp pair.hpp vector.hpp
all:main server client

main: command.o main.o alloc.o
	g++ command.o main.o alloc.o -o main
main.o: main.cpp command.h exceptions.h
	g++ -c main.cpp -o main.o --std=c++11 

server: server.o command.o alloc.o 
	g++ server.o command.o alloc.o -o server -lboost_system -lboost_thread -lpthread

server.o: server.cpp command.h exceptions.h
	g++ -c server.cpp -o server.o --std=c++11 

client: client.o command.o alloc.o
	g++ client.o command.o alloc.o -o client -lboost_system -lboost_thread -lpthread

client.o: client.cpp command.h exceptions.h
	g++ -c client.cpp -o client.o --std=c++11 

command.o: command.cpp $(data_structures) command.h TrainManager.hpp UserManager.hpp
	g++ -c command.cpp -o command.o --std=c++11 

alloc.o: alloc.cpp alloc.h exceptions.h
	g++ -c alloc.cpp -o alloc.o --std=c++11 


.PHONY : clean
clean:
	rm main server client *.o 
