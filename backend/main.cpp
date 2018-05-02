#include <iostream>
#include "user.hpp"

int main(){
	UserState userState;
	for(;!std::cin.eof();){
		read_command(userState, std::cin, std::cout);
	}
	return 0;
}
