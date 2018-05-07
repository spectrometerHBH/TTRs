#include <iostream>
#include <fstream>
#include "exceptions.h"
#include "bptree.hpp"
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

void read_command(std::istream & is, std::ostream & os) {
	String<50> command;
	while (is >> command) {
		// about user
		if (command == "register") {
			os << user_manager.sign_up(is, os) << '\n';
			continue;
		}
		if (command == "login") {
			os << user_manager.login(is, os) << '\n';
			continue;
		}
		if (command == "query_profile") {
			UserID user_id;
			is >> user_id;
			int flag = user_manager.query_profile(user_id, is, os);
			if (flag == 0) {
				os << 0 << '\n';
			}
			continue;
		}
		if (command == "modify_profile") {
			os << user_manager.modify_profile(is, os) << '\n';
			continue;
		}
		if (command == "modify_profile2") {
			os << user_manager.modify_profile2(is, os) << '\n';
			continue;
		}
		if (command == "privilege") {
			os << user_manager.get_privilege(is, os) << '\n';
			continue;
		}
		if (command == "modify_privilege") {
			UserID id1, id2;
			int privilege;
			is >> id1 >> id2 >> privilege;
			os << user_manager.modify_privilege(id1, id2, privilege, is, os) << '\n';
			continue;
		}
		// about ticket
		if (command == "query_ticket") {
			int flag = train_manager.query_ticket(is, os);
			if (flag == -1) {
				os << -1 << '\n';
			}
			continue;
		}
		// about train
		if (command == "add_train") {
			TrainID trian_id;
			is >> trian_id;
			os << train_manager.add_train(trian_id, is, os) << '\n';
			continue;
		}
		if (command == "sale_train") {
			TrainID train_id;
			is >> train_id;
			os << train_manager.sale_train(train_id, is, os) << '\n';
			continue;
		}
		if (command == "query_train") {
			TrainID train_id;
			is >> train_id;
			train_manager.query_train(train_id, is, os);
			continue;
		}
		if (command == "delete_train") {
			TrainID train_id;
			is >> train_id;
			os << train_manager.delete_train(train_id, is, os) << '\n';
			continue;
		}
		if (command == "modify_train") {
			os << train_manager.modify_train(is, os) << '\n';
			continue;
		}
		if (command == "clean") {
			train_manager.init();
			user_manager.init();
			os << 1 << '\n';
		}
		if (command == "exit") {
			os << "BYE\n";
			break;
		}
	}
}

/*
struct mykey {
	Location loc;
	int x;
};

class Less {
public:
	bool operator()(const mykey & a, const mykey & b) {
		if (a.loc < b.loc) return true;
		if (a.loc == b.loc && a.x < b.x) return true;
		return false;
	}
};

bool Less1(const mykey & a, const mykey & b) {
	return a.loc < b.loc;
}*/

/*
struct mykey {
	int f1, f2;
};

class Less {
public:
	bool operator()(const mykey & a, const mykey & b) {
		if (a.f1 < b.f1) return true;
		if (a.f1 == b.f1 && a.f2 < b.f2) return true;
		return false;
	}
};

bool Less1(const mykey & a, const mykey & b) {
	return a.f1 < b.f1;
}*/

int __main() {
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
	
	char ch[20];
	std::cin >> ch;
	int i = 0;
	while (ch[i] != '\0') {
	std::cout << ch[i] << '$';
	++i;
	}
	std::cout << '\n';
	std::cout << ch;
	*/
	/*
	bptree<mykey, int, 4096, Less> a("db", "index");
	a.init();
	int i, j;
	for (i = 0; i < 3; i++)
		for (j = 0; j < 3; j++) {
			mykey k;
			std::cin >> k.loc;
			k.x = j + i * 3;
			a.insert(k, 2 * i);
		}

	mykey stdkey;
	stdkey.loc = Location("sansan");
	stdkey.x = 1;
	sjtu::vector<sjtu::pair<mykey, int> > arr;
	a.search(arr, stdkey, Less1);
	for (i = 0; i < arr.size(); i++) {
		std::cout << arr[i].first.loc << ' ' << arr[i].first.x << std::endl;
	}*/
	/*
	bptree<mykey, int, 4096, Less> a("db", "index");
	a.init();
	int i, j;
	for (i = 0; i < 10; i++)
		for (j = 0; j < 10; j++) {
			mykey k = { i,j };
			a.insert(k, 2 * i);
		}

	mykey stdkey = { 4,5 };
	sjtu::vector<sjtu::pair<mykey, int> > arr;
	a.search(arr, stdkey, Less1);
	for (i = 0; i < arr.size(); i++) {
		std::cout << arr[i].first.f1 << ' ' << arr[i].first.f2 << std::endl;
	}
	*/
	read_command(std::cin, std::cout);
	return 0;
}






