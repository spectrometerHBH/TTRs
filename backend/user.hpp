#pragma once
#include <iostream>
#include <fstream>
#include "bptree.hpp"
#include "String.hpp"

struct UserInformation {
	String<20> name;
	String<20> password;
	String<20> email;
	String<20> phone;
	String<20> id;
	int privilege;
	UserInformation() : privilege(0) {}
	UserInformation(const String<20> & _id, const String<20> & _name, const String<20> & _password,
		const String<20> & _email, const String<20> & _phone, int _privilege) : 
		name(_name), password(_password), email(_email), phone(_phone), id(_id), privilege(_privilege) {}
};

std::ostream & operator<<(std::ostream & os, const UserInformation & info) ;

typedef bptree<String<20>, UserInformation> UserRecord;

class UserState {
private:
	UserRecord record;
	String<20> currentID;
	UserInformation info;
	std::ostream * uout;

	/*String<20> generateID() {
		for (int i = 19; i >= 0; --i) {
			if (currentID[i] < '9') {
				++currentID[i];
				return currentID;
			}
			else {
				currentID[i] = '0';
			}
		}
	}*/
	String<20> generateID(){
		int i, len  = strlen(currentID.getAddress());
		for (i = len - 1; i >= 0; --i) {
			if (currentID[i] < '9') {
				++currentID[i];
				return currentID;
			}
			else {
				currentID[i] = '0';
			}
		}
		for (i = len + 1; i >= 1; --i)
			currentID[i] = currentID[i-1];
		currentID[0] = '1';
		return currentID;
	}

	void update_id(){
		std::ofstream out("cntID.txt");
		if (!out.is_open()) *uout << "Failed in opening txt\n";
		out << currentID;
		out.close();
	}

public:
	UserState(std::ostream & _out = std::cout) : 
		record("userRecord", "indexUserRecord"), currentID("2017"), uout(&_out) {
		std::ifstream in("cntID.txt");
		if (!in.is_open()) {
			record.init();
		}
		else {
			in >> currentID;
			in.close();
		}
	}

	void set_out(std::ostream & _out){
		uout = &_out;
	}

	~UserState() {
		std::ofstream out("cntID.txt");
		//if (!out.is_open()) *uout << "Failed in opening txt\n";
		out << currentID;
		out.close();
	}

	int SignIn(const String<20> & _id, const String<20> & _password) {
		//if (info.priviledge != 0) {
		//	*uout << "Already sign in! \n";
		//	return -1;
		//}
		UserInformation result = record.find(_id);
		if (result.privilege != 0) {
			if (result.password == _password) {
				info = result;
				//*uout << "Sign in successfully!\n";
				*uout << "1\n";
				return 0;
			}
			else {
				//*uout << "Wrong password!\n";
				*uout << "0\n";
				return -1;
			}
		}
		else {
			//*uout << "No such id exists!\n";
			*uout << "0\n";
			return -1;
		}
	}

	int Logout() {
		if (info.privilege == 0) {
			*uout << "You haven't signed in!";
			return -1;
		}
		else {
			info = UserInformation();
			return 0;
		}
	}

	int Register(const String<20> & _name, const String<20> & _password, const String<20> & _email, const String<20> & _phone) {
		/*if (info.priviledge != 0) {
			*uout << "Please logout first!\n";
			return "";
		}*/
		String<20> _id = generateID();
		UserInformation registerInfo(_id, _name, _password, _email, _phone, 1);
		if (_id == "2018") registerInfo.privilege = 2;
		record.insert(_id, registerInfo);
		update_id();
		*uout << _id << "\n";
		return 1;
	}

	//for all users
	/*int QueryProfile() {
		if (info.priviledge == 0) {
			*uout << "Please sign in first!\n";
			return -1;
		}
		*uout << info.name << ' ' << info.email << ' ' << info.phone << '\n';
		return 0;
	}*/

	//for administrators
	int QueryProfile(const String<20> & _id) {
		/*if (info.priviledge != 2) {
			*uout << "Permission denied\n";
			return -1;
		}
		*/
		UserInformation result = record.find(_id);
		if (result.privilege == 0) {
			//*uout << "No such id exists!\n";
			*uout << "0\n";
			return -1;
		}
		*uout << result.name << ' ' << result.email << ' ' << result.phone << '\n';
		return 1;
	}

	//for all users
	/*int ModifyProfile(const String<20> & _name, const String<20> & _password, const String<20> & _email, const String<20> & _phone) {
		if (info.priviledge == 0) {
			*uout << "Please sign in first!\n";
			return -1;
		}
		info.name = _name;
		info.password = _password;
		info.email = _email;
		info.phone = _phone;
		record.set(info.id, info);
		return 0;
	}*/

	//for administrators
	int ModifyProfile(const String<20> & _id, const String<20> & _name, const String<20> & _password, const String<20> & _email, const String<20> & _phone) {
		/*if (info.priviledge != 2) {
			*uout << "Permission denied\n";
			return -1;
		}*/
		UserInformation result = record.find(_id);
		if (result.privilege == 0) {
			//*uout << "No such id exists!\n";
			*uout << "0\n";
			return 0;
		}
		result.name = _name;
		result.password = _password;
		result.email = _email;
		result.phone = _phone;
		record.set(_id, result);
		*uout << "1\n";
		return 1;
	}

		int ModifyProfile2(const String<20> & _id, const String<20> & _name, const String<20> & _email, const String<20> & _phone) {

		UserInformation result = record.find(_id);
		if (result.privilege == 0) {
			//*uout << "No such id exists!\n";
			*uout << "0\n";
			return 0;
		}
		result.name = _name;
		result.email = _email;
		result.phone = _phone;
		record.set(_id, result);
		*uout << "1\n";
		return 1;
	}

	int ModifyPrivilege(const String<20> & _id1, const String<20> _id2, int privilege) {
		/*if (info.priviledge != 2) {
			*uout << "Permission denied\n";
			return -1;
		}*/
		UserInformation admin = record.find(_id1);
		UserInformation result = record.find(_id2);
		if (admin.privilege == 0) {
			*uout << "0\n";
			return -1;
		}
		if (admin.privilege == 1) {
			*uout << "0\n";
			return -1;
		}
		if (result.privilege == 2 && privilege < 2){
			*uout << "0\n";
			return -1;			
		}
		result.privilege = privilege;
		record.set(_id2, result);
		*uout << "1\n";
		return 0;
	}

	void ListRecord() {
		record.traverse();
	}
};



int read_command(UserState & userState, std::istream &in, std::ostream &out);
