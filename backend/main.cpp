#include <iostream>
#include "command.h"

int main(){
    std::ios::sync_with_stdio(false);
    std::cin.tie(0);
    std::cout.tie(0);
///for(;!std::cin.eof();){
		read_command(std::cin, std::cout);
//	}
	return 0;
}
