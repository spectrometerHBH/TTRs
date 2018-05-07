#include <iostream>
#include <fstream>
#include "command.h"

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
			//puts("here");
			os << user_manager.modify_profile(is, os) << '\n';
			continue;
		}
		if (command == "modify_profile2") {
			//puts("here");
			os << user_manager.modify_profile(is, os) << '\n';
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
			train_manager.init(is, os);
			user_manager.init(is, os);
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



