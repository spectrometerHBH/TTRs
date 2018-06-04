#pragma once

#include <iostream>
#include <fstream>
#include "exceptions.h"
#include "bptree.hpp"
#include "String.hpp"
#include "date.hpp"

class UserManager {
private:
	struct User {
		UserID id;
		UserName name;
		Password password;
		Email email;
		Phone phone;
		int privilege;
		User() : privilege(1) {}
	};

	std::fstream iofile;
	String<20> user_file_name;
	UserID current_id;

	/*
	void generate_id() {
	for (int i = 19; i >= 0; --i) {
	if (current_id[i] < '9') {
	++current_id[i];
	break;
	}
	else {
	current_id[i] = '0';
	}
	}
	}
	*/

public:
	UserManager() : user_file_name("user_record") {
		iofile.open(user_file_name.getAddress());
		if (!iofile) {
			init();
			iofile.open(user_file_name.getAddress());
		}
		else {
			iofile.seekg(0, std::ios::beg);
			iofile.read(reinterpret_cast<char *> (&current_id), sizeof(UserID));
		}
	}

	~UserManager() {
		iofile.close();
	}

	void init() {
		std::ofstream out;
		out.open(user_file_name.getAddress());
		out.seekp(0, std::ios::end);
		current_id = 2017;
		out.write(reinterpret_cast<char *> (&current_id), sizeof(UserID));
		out.close();
	}
	

	UserID sign_up(std::istream & is = std::cin, std::ostream & os = std::cout) {
		User user;
		is >> user.name >> user.password >> user.email >> user.phone;
		++current_id;
		user.id = current_id;
		if (current_id == 2018) {
			user.privilege = 2;
		}
		iofile.seekp(0, std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&current_id), sizeof(UserID));
		iofile.seekp(0, std::ios::end);
		iofile.write(reinterpret_cast<char *> (&user), sizeof(User));
		return user.id;
	}

	int login(std::istream & is = std::cin, std::ostream & os = std::cout) {
		UserID user_id;
		Password password;
		is >> user_id >> password;
		if (user_id > current_id || user_id < 2018) {
			return 0;
		}
		iofile.seekg(sizeof(UserID) + (user_id - 2018) * sizeof(User), std::ios::beg);
		User user;
		iofile.read(reinterpret_cast<char *> (&user), sizeof(User));
		if (user.password == password) return 1;
		else return 0;
	}

	int query_profile(UserID & user_id, std::istream & is = std::cin, std::ostream & os = std::cout) {
		if (user_id > current_id || user_id < 2018) {
			return 0;
		}
		iofile.seekg(sizeof(UserID) + (user_id - 2018) * sizeof(User), std::ios::beg);
		User user;
		iofile.read(reinterpret_cast<char *> (&user), sizeof(User));
		os << user.name << ' ' << user.email << ' ' << user.phone << ' ' << user.privilege << '\n';
		return 1;
	}

	bool check_id(const UserID & user_id) {
		return (user_id >= 2018 && user_id <= current_id);
	}

	int modify_profile(std::istream & is = std::cin, std::ostream & os = std::cout) {
		User user;
		is >> user.id;
		if (user.id > current_id || user.id < 2018) {
			return 0;
		}
		iofile.seekg(sizeof(UserID) + (user.id - 2018) * sizeof(User), std::ios::beg);
		iofile.read(reinterpret_cast<char *> (&user), sizeof(User));
		is >> user.name >> user.password >> user.email >> user.phone;
		
		iofile.seekp(sizeof(UserID) + (user.id - 2018) * sizeof(User), std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&user), sizeof(User));
		return 1;
	}

	int modify_profile2(std::istream & is = std::cin, std::ostream & os = std::cout) {                   // need not modify password
		User user;
		UserID id;
		is >> id;
		if (id > current_id || id < 2018) {
			return 0;
		}

		iofile.seekg(sizeof(UserID) + (id - 2018) * sizeof(User), std::ios::beg);
		iofile.read(reinterpret_cast<char *> (&user), sizeof(User));

		is >> user.name >> user.email >> user.phone;

		iofile.seekp(sizeof(UserID) + (id - 2018) * sizeof(User), std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&user), sizeof(User));

		return 1;
	}

	int get_privilege(std::istream & is = std::cin, std::ostream & os = std::cout) {
		User user;
		UserID id;
		is >> id;
		if (id > current_id || id < 2018) {
			return 0;
		}
		iofile.seekg(sizeof(UserID) + (id - 2018) * sizeof(User), std::ios::beg);
		iofile.read(reinterpret_cast<char *> (&user), sizeof(User));
		return user.privilege;
	}

	int modify_privilege(UserID & id1, UserID & id2, int privilege, std::istream & is = std::cin, std::ostream & os = std::cout) {
		if (id1 > current_id || id1 < 2018 || id2 > current_id || id2 < 2018) {
			return 0;
		}
		User user1, user2;
		iofile.seekg(sizeof(UserID) + (id1 - 2018) * sizeof(User), std::ios::beg);
		iofile.read(reinterpret_cast<char *> (&user1), sizeof(User));
		if (user1.privilege == 1) {
			return 0;
		}
		iofile.seekg(sizeof(UserID) + (id2 - 2018) * sizeof(User), std::ios::beg);
		iofile.read(reinterpret_cast<char *> (&user2), sizeof(User));
		if (user2.privilege == 2) {
            if (privilege == 2) return 1;
			return 0;
		}
		user2.privilege = privilege;
		iofile.seekp(sizeof(UserID) + (id2 - 2018) * sizeof(User), std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&user2), sizeof(User));
		return 1;
	}

};
