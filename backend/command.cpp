#include <iostream>
#include <fstream>
#include "bplus_tree\exceptions.h"
#include "bplus_tree\bptree.hpp"
#include "String.hpp"
#include "UserManager.hpp"
#include "TrainManager.hpp"
#include "date.hpp"

UserManager user_manager;
TrainManager train_manager;

/*
void init() {
	train_manager.init();
}

void init_switch() {
	int init_sign;
	std::fstream iofile;
	iofile.open("init");
	if (!iofile) {
		std::ofstream out;
		out.open("init");
		init();
		init_sign = 1;
		out.seekp(std::ios::beg);
		out.write(reinterpret_cast<char *> (&init_sign), sizeof(init_sign));
		out.close();
		return;
	}
	iofile.seekg(std::ios::beg);
	iofile.read(reinterpret_cast<char *> (&init_sign), sizeof(init_sign));
	if (init_sign == 0) {
		init();
		init_sign = 1;
		iofile.seekp(std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&init_sign), sizeof(init_sign));
	}
	iofile.close();
}
*/

void read_command() {
	String<50> command;
	while (std::cin >> command) {
		if (command == "register") {
			std::cout << user_manager.sign_up() << '\n';
			continue;
		}
		if (command == "login") {
			std::cout << user_manager.login() << '\n';
			continue;
		}
		if (command == "query_profile") {
			UserID user_id;
			std::cin >> user_id;
			int flag = user_manager.query_profile(user_id);
			if (flag == 0) {
				std::cout << 0 << '\n';
			}
			continue;
		}
		if (command == "modify_profile") {
			std::cout << user_manager.modify_profile() << '\n';
			continue;
		}
		if (command == "modify_privilege") {
			UserID id1, id2;
			int privilege;
			std::cin >> id1 >> id2 >> privilege;
			std::cout << user_manager.modify_privilege(id1, id2, privilege) << '\n';
			continue;
		}
		if (command == "add_train") {
			std::cout << train_manager.add_train() << '\n';
			continue;
		}
		if (command == "sale_train") {
			TrainID train_id;
			std::cin >> train_id;
			std::cout << train_manager.safe_train(train_id) << '\n';
			continue;
		}
		if (command == "query_train") {
			TrainID train_id;
			std::cin >> train_id;
			train_manager.query_train(train_id);
			continue;
		}
		if (command == "delete_train") {
			TrainID train_id;
			std::cin >> train_id;
			std::cout << train_manager.delete_train(train_id) << '\n';
			continue;
		}
		if (command == "modify_train") {
			std::cout << train_manager.modify_train() << '\n';
			continue;
		}
		if (command == "exit") {
			break;
		}
	}
}

int main() {
	/*
	init_switch();
	
	std::ofstream shu("kkk");
	shu << "dfdfg\n";
	String<5> s1("kkkkk");
	String<3> s2("rrr");
	String<8> s;
	s = s1 + s2;
	std::cout << s << '\n';
	
	int a;
	std::cin >> a;
	std::cout << a << '\n';
	

	Date today(2018, 5, 29);
	for (int i = 0; i < 10; ++i) {
		++today;
		std::cout << today << '\n';
	}
	
	std::cout << '$' << '\n';
	
	train_manager.init();
	
	train_manager.add_train();
	
	TrainID train_id;

	std::cin >> train_id;
	train_manager.query_train(train_id);
	
	bptree<int, int> test("int", "ind");
	test.init();
	std::cout << test.count(1) << '\n';
	std::cout << test.find(1) << '\n';
	*/
	read_command();
	return 0;
}