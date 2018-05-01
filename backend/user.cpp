#include <iostream>
#include <fstream>
#include <cstring>
#include "user.h"

int read_command(UserState & userState, std::istream &in, std::ostream &out) {
	String<20> word, id, id2, name, password, email, phone;
	in >> word;
	if (word == "exit") return -1;
	if (word == "register") {
		in >> name >> password >> email >> phone;
		userState.Register(name, password, email, phone) << '\n';
		return 0;
	}
	if (word == "login") {
		in >> id >> password;
		userState.SignIn(id, password);
		return 0;
	}
	/*if (word == "logout") {
		userState.Logout();
		return 0;
	}
	if (word == "query_myprofile") {
		userState.QueryProfile();
		return 0;
	}*/
	if (word == "query_profile") {
		in >> id;
		userState.QueryProfile(id);
		return 0;
	}
	/*if (word == "modify_myprofile") {
		in >> name >> password >> email >> phone;
		userState.ModifyProfile(name, password, email, phone);
		return 0;
	}*/
	if (word == "modify_profile") {
		//puts("modify");
		in >> id >> name >> password >> email >> phone;
		userState.ModifyProfile(id, name, password, email, phone);
		return 0;
	}

	if (word == "modify_profile2") {
		//puts("modify");
		in >> id >> name >> email >> phone;
		userState.ModifyProfile2(id, name, email, phone);
		return 0;
	}

	if (word == "modify_privilege") {
		in >> id >> id2;
		int p;
		in >> p;
		userState.ModifyPrivilege(id, id2, p);
		return 0;
	}
	if (word == "list_record") {
		userState.ListRecord();
		return 0;
	}
	return -2;
}

std::ostream & operator<<(std::ostream & os, const UserInformation & info) {
	os << info.id << '\n' << info.name << '\n' << info.email << '\n' << info.phone << '\n' << info.privilege << '\n';
	return os;
}

int test() {
	UserState userState(std::cout); // Here std::cout can be changed to any specific std::ostream

	
	/*std::cout << userState.Register("yifan", "mypassword", "xuyifan@gmail.com", "13262934378") << '\n';
	userState.SignIn("00000000000000000009", "424242");
	userState.QueryProfile();
	userState.ModifyProfile("greeneyes", "424242", "greeneyes@gmail.com", "15355758761");
	userState.QueryProfile();
	userState.Logout();
	*/

	int r = 0;
	while (r != -1) {
		r = read_command(userState, std::cin, std::cout);
	}

	return 0;
}