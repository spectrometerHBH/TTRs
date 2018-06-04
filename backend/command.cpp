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
		if (command == "query_transfer") {
			int flag = train_manager.query_transfer(is, os);
			if (flag == -1) {
				os << -1 << '\n';
			}
			continue;
		}
		if (command == "buy_ticket") {
			UserID user_id;
			is >> user_id;
			if (user_manager.check_id(user_id) == false) {
				os << 0 << '\n';
			}
			else {
				os << train_manager.buy_ticket(user_id, is, os) << '\n';
			}
			continue;
		}
		if (command == "query_order") {
			UserID user_id;
			is >> user_id;
			if (user_manager.check_id(user_id) == false) {
				Date date;
				CatalogList catalog_list;
				is >> date >> catalog_list;
				os << -1 << '\n';
			}
			else {
				if (train_manager.query_order(user_id, is, os) == -1) {
					os << -1 << '\n';
				}
			}
			continue;
		}
		if (command == "refund_ticket") {
			UserID user_id;
			is >> user_id;
			if (user_manager.check_id(user_id) == false) {
				int num;
				TrainID train_id;
				Location loc1, loc2;
				Date date;
				Seat seat_kind;
				is >> num >> train_id >> loc1 >> loc2 >> date >> seat_kind;
				os << 0 << '\n';
			}
			else {
				os << train_manager.refund_ticket(user_id, is, os) << '\n';
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
		//query_train including sale only
		if (command == "query_train") {
			TrainID train_id;
			is >> train_id;
			int flag = train_manager.query_train(train_id, is, os);
			if (flag == 0) {
				os << 0 << '\n';
			}
			continue;
		}
		//query_train including sale & unsale
		if (command == "query_train2") {
			TrainID train_id;
			is >> train_id;
			int flag = train_manager.query_train2(train_id, is, os);
			if (flag == 0) {
				os << 0 << '\n';
			}
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
		// others
		if (command == "list_station") {
			train_manager.list_station(is, os);
			continue;
		}
		if (command == "list_unsale_train") {
			train_manager.list_unsale_train(is, os);
			continue;
		}
	}
}


int __main() {
	read_command(std::cin, std::cout);
	return 0;
}






