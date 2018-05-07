#pragma once

#include <iostream>
#include <fstream>
#include <functional>
#include "exceptions.h"
#include "bptree.hpp"
#include "vector.hpp"
#include "pair.hpp"
#include "String.hpp"
#include "date.hpp"

class TrainManager {
private:
	struct Train{
		TrainID id;
		TrainName name;
		Catalog catalog;
		int station_num;
		int ticket_num;
		long route_pos;                    // position of this train's route in route_file 
		long ticket_price_pos;             // position of this train's ticket_price in ticket_price_file
		long ticket_left_pos;              // position of this train's ticket_left in ticket_left_file
		Ticket ticket[10];
		int open;
		int sale;
		Train() : station_num(0), ticket_num(0), open(0), sale(0) {}
	};

	struct Station {
		Location loc;
		Time arrive;
		Time depart;
		Time stop;
	};

	typedef bptree<TrainID, Train> TrainRecord;

	struct BindKey {
		Location loc;
		TrainID train_id;
		BindKey(const Location & _loc = Location(), const TrainID & _train_id = TrainID()) : loc(_loc), train_id(_train_id) {}
		bool operator==(const BindKey & other) {
			return (loc == other.loc && train_id == other.train_id);
		}
		bool operator<(const BindKey & other) const {
			if (loc < other.loc) return 1;
			if (other.loc < loc) return 0;
			return train_id < other.train_id;
		}
	};

	typedef bptree<BindKey, Catalog> StationRecord;

	TrainRecord train_record;
	StationRecord station_record;
	String<20> route_file;
	String<20> ticket_price_file;
	String<20> ticket_left_file;

	// for a specific train, query ticket.
	void query_ticket(const TrainID & train_id, const Date & date, const Location & loc1, const Location & loc2, std::istream & is = std::cin,	std::ostream & os = std::cout) {
		
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return;
		if (train.open == 0) return;
		
		std::fstream iofile;

		Station * s_array = new Station[train.station_num];          // s_array: station_array
		iofile.open(route_file.getAddress());
		iofile.seekg(train.route_pos, std::ios::beg);
		iofile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);
		iofile.close();

		int s1, s2;
		for (int i = 0; i < train.station_num; ++i) {
			if (s_array[i].loc == loc1) {
				s1 = i;
				break;
			}
		}
		for (int i = 0; i < train.station_num; ++i) {
			if (s_array[i].loc == loc2) {
				s2 = i;
				break;
			}
		}
		if (s1 >= s2) {
			delete[] s_array;
			return;
		}

		int tp_num = train.ticket_num * train.station_num;           // tp_num: ticket_price_num 
		double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
		iofile.open(ticket_price_file.getAddress());
		iofile.seekg(train.ticket_price_pos, std::ios::beg);
		iofile.read(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);
		iofile.close();

		int day = date - start_date;
		int tl_num = train.ticket_num * train.station_num;                   // tl_num: ticket_left_num
		int * tl_array = new int[tl_num];                                    // tl_array: ticket_left_array
		iofile.open(ticket_left_file.getAddress());
		iofile.seekg(train.ticket_left_pos + day * tl_num, std::ios::beg);
		iofile.read(reinterpret_cast<char *> (tl_array), sizeof(int) * tl_num);
		iofile.close();

		os << train.id << ' ' << loc1 << ' ' << date << ' ' << s_array[s1].depart
			<< ' ' << loc2 << ' ' << date << ' ' << s_array[s2].arrive << ' ';
		for (int i = 0; i < train.ticket_num; ++i) {
			int ticket_left = 2000;
			double ticket_price = 0;
			for (int j = s1; j <= s2; ++j) {
				if (tl_array[j + i * train.station_num] < ticket_left) {
					ticket_left = tl_array[j + i * train.station_num];
				}
				ticket_price += tp_array[j + i * train.station_num];
			}
			os << train.ticket[i] << ' ' << ticket_left << ' ' << ticket_price << ' ';
		}
		os << '\n';

		delete[] s_array;
		delete[] tp_array;
		delete[] tl_array;
	}

public:
	TrainManager() : 
		train_record("train_record", "index_train_record"), station_record("station_record", "index_station_record"),
		route_file("route_record"), ticket_price_file("ticket_price_record"), ticket_left_file("ticket_left_record") {
		std::fstream iofile;
		iofile.open(route_file.getAddress());
		if (!iofile) {
			init();
		}
		else {
			iofile.close();
		}
	}

	void init(std::istream & is = std::cin,	std::ostream & os = std::cout) {
		train_record.init();
		station_record.init();
		std::ofstream out;
		out.open(route_file.getAddress());
		out.close();
		out.open(ticket_price_file.getAddress());
		out.close();
		out.open(ticket_left_file.getAddress());
		out.close();
	}
	
	int sale_train(const TrainID & train_id, std::istream & is = std::cin,	std::ostream & os = std::cout) {
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		if (train.open == 1) return 0;
		train.open = 1;
		train_record.set(train_id, train);
		return 1;
	}

	int query_train(const TrainID & train_id, std::istream & is = std::cin,	std::ostream & os = std::cout) {

		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		//if (train.open == 0) return 0;
		os << train.id << ' ' << train.name << ' ' << train.catalog 
			<< ' ' << train.station_num << ' ' << train.ticket_num << ' ';
		for (int i = 0; i < train.ticket_num; ++i) {
			os << train.ticket[i] << ' ';
		}
		os << '\n';
		
		std::fstream iofile;
		
		iofile.open(route_file.getAddress());
		iofile.seekg(train.route_pos, std::ios::beg);
		Station * s_array = new Station[train.station_num];          // s_array: station_array
		iofile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);
		iofile.close();

		iofile.open(ticket_price_file.getAddress());
		iofile.seekg(train.ticket_price_pos, std::ios::beg);
		int tp_num = train.ticket_num * train.station_num;           // tp_num: ticket_price_num 
		double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
		iofile.read(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);
		iofile.close();

		for (int i = 0; i < train.station_num; ++i) {
			os << s_array[i].loc << ' ' << s_array[i].arrive << ' ' 
				<< s_array[i].depart << ' ' << s_array[i].stop << ' ';
			for (int j = 0; j < train.ticket_num; ++j) {
				os << '$' << tp_array[i + j * train.station_num] << ' ';
			}
			os << '\n';
		}

		delete[] s_array;
		delete[] tp_array;

		return 1;
	}
	
	int add_train(const TrainID & train_id, std::istream & is = std::cin,	std::ostream & os = std::cout) {

		Train train;
		train.id = train_id;
		is >> train.name >> train.catalog >> train.station_num >> train.ticket_num;
		//if (train_record.count(train.id) == 1) return 0;
		for (int i = 0; i < train.ticket_num; ++i) {
			is >> train.ticket[i];
		}

		Station * s_array = new Station[train.station_num];          // s_array: station_array
		int tp_num = train.ticket_num * train.station_num;           // tp_num: ticket_price_num 
		double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
		
		for (int i = 0; i < train.station_num; ++i) {
			is >> s_array[i].loc >> s_array[i].arrive >> s_array[i].depart >> s_array[i].stop;
			char ch;
			for (int j = 0; j < train.ticket_num; ++j) {
				is >> ch >> ch >> ch >> tp_array[i + j * train.station_num];
			}
			BindKey bind_key(s_array[i].loc, train.id);
			station_record.insert(bind_key, train.catalog);
		}

		std::fstream iofile;
		
		iofile.open(route_file.getAddress());
		iofile.seekp(0, std::ios::end);
		train.route_pos = iofile.tellp();
		iofile.write(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);
		iofile.close();
		
		iofile.open(ticket_price_file.getAddress());
		iofile.seekp(0, std::ios::end);
		train.ticket_price_pos = iofile.tellp();
		iofile.write(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);
		iofile.close();
		
		delete[] s_array;
		delete[] tp_array;
		
		int day_num = (end_date - start_date) + 1;
		int tl_num = day_num * train.ticket_num * train.station_num;         // tl_num: ticket_left_num
		
		int * tl_array = new int[tl_num];                                    // tl_array: ticket_left_array
		for (int i = 0; i < tl_num; ++i) {
			tl_array[i] = 2000;
		}

		iofile.open(ticket_left_file.getAddress());
		iofile.seekp(0, std::ios::end);
		train.ticket_left_pos = iofile.tellp();
		iofile.write(reinterpret_cast<char *> (tl_array), sizeof(int) * tl_num);
		iofile.close();

		delete[] tl_array;
		
		train_record.insert(train.id, train);
		
		return 1;
	}
	

	int delete_train(TrainID & train_id, std::istream & is = std::cin,	std::ostream & os = std::cout) {
		Train train = train_record.find(train_id);
		if (train.station_num = 0) return 0;
		if (train.sale == 1) return 0;
	
		std::fstream iofile;
		iofile.open(route_file.getAddress());
		iofile.seekg(train.route_pos, std::ios::beg);
		Station * s_array = new Station[train.station_num];           //s_array: station_array
		iofile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);
		iofile.close();
		
		for (int i = 0; i < train.station_num; ++i) {
			BindKey bind_key(s_array[i].loc, train.id);
			station_record.remove(bind_key);
		}

		delete[] s_array;
		
		train_record.remove(train_id);
		
		return 1;
	}

	int modify_train(std::istream & is = std::cin,	std::ostream & os = std::cout) {
		TrainID train_id;
		is >> train_id;
		int flag = delete_train(train_id);
		if (flag == 0) return 0;
		return add_train(train_id);
	}

	int query_ticket(std::istream & is = std::cin,	std::ostream & os = std::cout) {
		Location loc1, loc2;
		Date date;
		Catalog catalog;
		is >> loc1 >> loc2 >> date >> catalog;
		BindKey bk1(loc1), bk2(loc2);
		sjtu::vector<sjtu::pair<BindKey, Catalog> > p_array1, p_array2;       // p_array: array of train_id that pass the station
		auto same_loc = [](const BindKey & bind_key1, const BindKey & bind_key2)->bool { return bind_key1.loc < bind_key2.loc; };
		station_record.search(p_array1, bk1, same_loc);
		station_record.search(p_array2, bk2, same_loc);

		sjtu::vector<TrainID> t_array;                                        // t_array: train which may statisfy condition
		auto iter1 = p_array1.begin(), iter2 = p_array2.begin();
		while (iter1 != p_array1.end() && iter2 != p_array2.end()) {
			TrainID id1((*iter1).first.train_id), id2((*iter2).first.train_id);
			if (id1 < id2) {
				++iter1;
				continue;
			}
			if (id2 < id1) {
				++iter2;
				continue;
			}
			if ((*iter1).second == catalog) {
				t_array.push_back(id1);
			}
			++iter1;
			++iter2;
		}

		if (t_array.empty()) return -1;
		
		for (auto iter = t_array.begin(); iter != t_array.end(); ++iter) {
			query_ticket(*iter, date, loc1, loc2);
		}

		return 1;
	}

	int query_transfer() {}

	int buy_ticket() {}

	int refund_ticket() {}

};


