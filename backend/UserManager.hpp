#pragma once

#include <iostream>
#include <fstream>
#include "bplus_tree\exceptions.h"
#include "bplus_tree\bptree.hpp"
#include "String.hpp"

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

	std::istream & is;
	std::ostream & os;
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
	UserManager(std::istream & _is = std::cin, std::ostream & _os = std::cout) : is(_is), os(_os), user_file_name("user_record") {
		std::fstream iofile;
		iofile.open(user_file_name.getAddress());
		if (!iofile) {
			init();
		}
		else {
			iofile.seekg(0, std::ios::beg);
			iofile.read(reinterpret_cast<char *> (&current_id), sizeof(UserID));
			iofile.close();
		}
	}

	void init() {
		std::ofstream out;
		out.open(user_file_name.getAddress());
		out.seekp(0, std::ios::end);
		current_id = 2018;
		out.write(reinterpret_cast<char *> (&current_id), sizeof(UserID));
		User default_admin;
		default_admin.id = current_id;
		default_admin.password = "123456";
		default_admin.privilege = 2;
		out.write(reinterpret_cast<char *> (&default_admin), sizeof(User));
		out.close();
	}
	

	UserID sign_up() {
		User user;
		is >> user.name >> user.password >> user.email >> user.phone;
		++current_id;
		user.id = current_id;
		std::fstream iofile;
		iofile.open(user_file_name.getAddress());
		iofile.seekp(0, std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&current_id), sizeof(UserID));
		iofile.seekp(0, std::ios::end);
		iofile.write(reinterpret_cast<char *> (&user), sizeof(User));
		iofile.close();
		return user.id;
	}

	int login() {
		UserID user_id;
		Password password;
		is >> user_id >> password;
		if (user_id > current_id || user_id < 2018) {
			return 0;
		}
		std::fstream iofile;
		iofile.open(user_file_name.getAddress());
		iofile.seekg(sizeof(UserID) + (user_id - 2018) * sizeof(User), std::ios::beg);
		User user;
		iofile.read(reinterpret_cast<char *> (&user), sizeof(User));
		iofile.close();
		if (user.password == password) return 1;
		else return 0;
	}

	int query_profile(UserID & user_id) {
		if (user_id > current_id || user_id < 2018) {
			return 0;
		}
		std::fstream iofile;
		iofile.open(user_file_name.getAddress());
		iofile.seekg(sizeof(UserID) + (user_id - 2018) * sizeof(User), std::ios::beg);
		User user;
		iofile.read(reinterpret_cast<char *> (&user), sizeof(User));
		iofile.close();
		os << user.name << ' ' << user.email << ' ' << user.phone << '\n';
		return 1;
	}

	int modify_profile() {
		User user;
		is >> user.id;
		if (user.id > current_id || user.id < 2018) {
			return 0;
		}
		is >> user.name >> user.password >> user.email >> user.phone;
		std::fstream iofile;
		iofile.open(user_file_name.getAddress());
		iofile.seekp(sizeof(UserID) + (user.id - 2018) * sizeof(User), std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&user), sizeof(User));
		iofile.close();
		return 1;
	}

	int modify_privilege(UserID & id1, UserID & id2, int privilege) {
		if (id1 > current_id || id1 < 2018 || id2 > current_id || id2 < 2018) {
			return 0;
		}
		User user1, user2;
		std::fstream iofile;
		iofile.open(user_file_name.getAddress());
		iofile.seekg(sizeof(UserID) + (id1 - 2018) * sizeof(User), std::ios::beg);
		iofile.read(reinterpret_cast<char *> (&user1), sizeof(User));
		if (user1.privilege == 1) {
			return 0;
		}
		iofile.seekg(sizeof(UserID) + (id2 - 2018) * sizeof(User), std::ios::beg);
		iofile.read(reinterpret_cast<char *> (&user2), sizeof(User));
		if (user2.privilege == 2) {
			return 0;
		}
		user2.privilege = privilege;
		iofile.seekp(sizeof(UserID) + (id2 - 2018) * sizeof(User), std::ios::beg);
		iofile.write(reinterpret_cast<char *> (&user2), sizeof(User));
		iofile.close();
		return 1;
	}
};