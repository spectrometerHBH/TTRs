#pragma once

#include <iostream>
#include <fstream>
#include <functional>
#include "exceptions.h"
#include "bptree.hpp"
#include "vector.hpp"
#include "map.hpp"
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
		int seat_num;
		long route_pos;                    // position of this train's route in route_file 
		long ticket_price_pos;             // position of this train's ticket_price in ticket_price_file
		long ticket_left_pos;              // position of this train's ticket_left in ticket_left_file
		Seat seat[12];
		int open;
		int sale;
		Train() : station_num(0), seat_num(0), open(0), sale(0) {}
	};

	struct Station {
		Location loc;
		Time arrive;
		Time depart;
		Time stop;
		int day = 0;
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
			if (loc > other.loc) return 0;
			return train_id < other.train_id;
		}
	};

	struct BindValue {
		int station_no;
		Catalog catalog;
		BindValue(int _no = -1, const Catalog & _catalog = Catalog()) : station_no(_no), catalog(_catalog) {}
	};

	typedef bptree<BindKey, BindValue> StationRecord;

	struct TicketKey {
		UserID user_id;
		Date date;
		Catalog catalog;
		TrainID train_id;
		Location from;
		Location to;
		TicketKey(const UserID & _user_id = UserID(), const Date & _date = Date(), Catalog _catalog = Catalog(), 
			const TrainID & _train_id = TrainID(), const Location & _from = Location(), const Location & _to = Location()) :
			user_id(_user_id), date(_date), catalog(_catalog), train_id(_train_id), from(_from), to(_to) {}
		bool operator==(const TicketKey & other) {
			return (user_id == other.user_id && date == other.date && catalog == other.catalog 
				&& train_id == other.train_id && from == other.from && to == other.to);
		}
		bool operator<(const TicketKey & other) const {
			if (user_id < other.user_id) return 1;
			if (user_id > other.user_id) return 0;
			if (date < other.date) return 1;
			if (date > other.date) return 0;
			if (catalog < other.catalog) return 1;
			if (catalog > other.catalog) return 0;
			if (train_id < other.train_id) return 1;
			if (train_id > other.train_id) return 0;
			if (from < other.from) return 1;
			if (from > other.from) return 0;
			return to < other.to;
		}
	};

	struct TicketValue {
		int day_from = 0;
		int day_to = 0;
		Time depart;
		Time arrive;
		Seat seat[12];
		double price[12];
		int num[12] = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		TicketValue(const Time & _depart = Time("xx:xx"), const Time & _arrive = Time("xx:xx")) :
			depart(_depart), arrive(_arrive) {}
	};

	typedef bptree<TicketKey, TicketValue> OrderRecord;

	TrainRecord train_record;
	StationRecord station_record;
	OrderRecord order_record;
	String<20> route_file;
	String<20> ticket_price_file;
	String<20> ticket_left_file;

	std::fstream rfile;
	std::fstream tpfile;
	std::fstream tlfile;
						
	struct HalfRoute {
		Train train;
		int station_no;
		Station * station_list = nullptr;
		HalfRoute(const Train & _trian, int _no, Station * _list = nullptr) :
			train(_trian), station_no(_no), station_list(_list) {}
	};
	
	// for a specific train, query ticket.
	void query_ticket(const Train & train, const Date & date, int s1_no, int s2_no, std::istream & is = std::cin, std::ostream & os = std::cout) {

		Station * s_array = new Station[train.station_num];          // s_array: station_array
		rfile.seekg(train.route_pos, std::ios::beg);
		rfile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);

		int tp_num = train.seat_num * train.station_num;             // tp_num: ticket_price_num 
		double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
		tpfile.seekg(train.ticket_price_pos, std::ios::beg);
		tpfile.read(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);

		int day_no = date - start_date;
		int tl_num = train.seat_num * train.station_num;                     // tl_num: ticket_left_num
		int * tl_array = new int[tl_num];                                    // tl_array: ticket_left_array
		tlfile.seekg(train.ticket_left_pos + day_no * tl_num * sizeof(int), std::ios::beg);
		tlfile.read(reinterpret_cast<char *> (tl_array), sizeof(int) * tl_num);

		Date date1 = date, date2 = date;
		if (s_array[s1_no].day == 1) ++date1;
		if (s_array[s2_no].day == 1) ++date2;
		os << train.id << ' ' << s_array[s1_no].loc << ' ' << date1 << ' ' << s_array[s1_no].depart 
			<< ' ' << s_array[s2_no].loc << ' ' << date2 << ' ' << s_array[s2_no].arrive;
		for (int i = 0; i < train.seat_num; ++i) {
			int ticket_left = 2000;
			double ticket_price = 0;
			for (int j = s1_no + 1; j <= s2_no; ++j) {
				if (tl_array[j + i * train.station_num] < ticket_left) {
					ticket_left = tl_array[j + i * train.station_num];
				}
				ticket_price += tp_array[j + i * train.station_num];
			}
			os << ' ' << train.seat[i] << ' ' << ticket_left << ' ' << ticket_price;
		}
		os << '\n';

		delete[] s_array;
		delete[] tp_array;
		delete[] tl_array;
	}

	bool in_catalog_list(char catalog, CatalogList & catalog_list) {
		bool flag = false;
		int i = 0;
		while (catalog_list[i] != '\0') {
			if (catalog == catalog_list[i]) {
				return true;
			}
			++i;
		}
		return false;
	}

	int make_transfer(const HalfRoute & first_train, const HalfRoute & second_train, int & hub1, int & hub2) {
		Time set_off1 = first_train.station_list[0].depart, set_off2 = second_train.station_list[0].depart;
		sjtu::map<Location, int> hub_map;
		for (int i = first_train.station_no + 1; i < first_train.train.station_num; ++i) {
			hub_map[first_train.station_list[i].loc] = i;
		}
		int flag = 0;
		for (int i = 0; i < second_train.station_no; ++i) {
			if (hub_map.count(second_train.station_list[i].loc) == 1) {
				hub1 = hub_map[second_train.station_list[i].loc];
				hub2 = i;
				Time arrive_hub = first_train.station_list[hub1].arrive,
					depart_hub = second_train.station_list[hub2].depart;
				if (arrive_hub != "xx:xx" && depart_hub != "xx:xx" && arrive_hub < depart_hub) {
					flag = 1;
					break;
				}
			}
		}
		return flag;
	}

	void show_ticket(const Train & train, int s1_no, int s2_no, Station * station_list, const Date & date, std::istream & is = std::cin, std::ostream & os = std::cout) {
		Date date1 = date, date2 = date;
		if (station_list[s1_no].day == 1) ++date1;
		if (station_list[s2_no].day == 1) ++date2;
		os << train.id << ' ' << station_list[s1_no].loc << ' ' << date1 << ' ' << station_list[s1_no].depart;
		os << ' ' << station_list[s2_no].loc << ' ' << date2 << ' ' << station_list[s2_no].arrive;
		int ticket_num = train.seat_num * train.station_num;
		double * tp_array = new double[ticket_num];
		tpfile.seekg(train.ticket_price_pos, std::ios::beg);
		tpfile.read(reinterpret_cast<char *> (tp_array), sizeof(double) * ticket_num);
		int day_no = date - start_date;
		int * tl_array = new int[ticket_num];
		tlfile.seekg(train.ticket_left_pos + day_no * ticket_num * sizeof(int), std::ios::beg);
		tlfile.read(reinterpret_cast<char *> (tl_array), sizeof(int) * ticket_num);
		for (int i = 0; i < train.seat_num; ++i) {
			double ticket_price = 0;
			int ticket_left = 2000;
			for (int j = s1_no + 1; j <= s2_no; ++j) {
				ticket_price += tp_array[j + i * train.station_num];
				if (tl_array[j + i * train.station_num] < ticket_left) {
					ticket_left = tl_array[j + i * train.station_num];
				}
			}
			os << ' ' << train.seat[i] << ' ' << ticket_left << ' ' << ticket_price;
		}
		os << '\n';
		delete[] tp_array;
		delete[] tl_array;
	}

public:
	TrainManager() : train_record("train_record", "index_train_record"), 
		station_record("station_record", "index_station_record"),
		order_record("order_record", "index_order_record"),
		route_file("route_record"), ticket_price_file("ticket_price_record"), ticket_left_file("ticket_left_record") {
		std::fstream iofile;
		iofile.open(route_file.getAddress());
		if (!iofile) {
			init();
		}
		else {
			iofile.close();
		}
		rfile.open(route_file.getAddress());
		tpfile.open(ticket_price_file.getAddress());
		tlfile.open(ticket_left_file.getAddress());
	}

	~TrainManager() {
		rfile.close();
		tpfile.close();
		tlfile.close();
	}

	void init() {
		train_record.init();
		station_record.init();
		order_record.init();
		std::ofstream out;
		out.open(route_file.getAddress());
		out.close();
		out.open(ticket_price_file.getAddress());
		out.close();
		out.open(ticket_left_file.getAddress());
		out.close();
	}
	
	int sale_train(const TrainID & train_id, std::istream & is = std::cin, std::ostream & os = std::cout) {
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		if (train.open == 1) return 0;
		train.open = 1;
		train_record.set(train_id, train);
		return 1;
	}

	int query_train(const TrainID & train_id, std::istream & is = std::cin, std::ostream & os = std::cout) {

		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		if (train.open == 0) return 0;
		os << train.id << ' ' << train.name << ' ' << train.catalog 
			<< ' ' << train.station_num << ' ' << train.seat_num;
		for (int i = 0; i < train.seat_num; ++i) {
			os << ' ' << train.seat[i];
		}
		os << '\n';
		
		rfile.seekg(train.route_pos, std::ios::beg);
		Station * s_array = new Station[train.station_num];          // s_array: station_array
		rfile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);

		tpfile.seekg(train.ticket_price_pos, std::ios::beg);
		int tp_num = train.seat_num * train.station_num;           // tp_num: ticket_price_num 
		double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
		tpfile.read(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);

		for (int i = 0; i < train.station_num; ++i) {
			os << s_array[i].loc << ' ' << s_array[i].arrive << ' ' 
				<< s_array[i].depart << ' ' << s_array[i].stop;
			for (int j = 0; j < train.seat_num; ++j) {
				os << ' ' << "￥" << tp_array[i + j * train.station_num];
			}
			os << '\n';
		}

		delete[] s_array;
		delete[] tp_array;

		return 1;
	}

	int query_train2(const TrainID & train_id, std::istream & is = std::cin, std::ostream & os = std::cout) {

		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		os << train.id << ' ' << train.name << ' ' << train.catalog
			<< ' ' << train.station_num << ' ' << train.seat_num;
		for (int i = 0; i < train.seat_num; ++i) {
			os << ' ' << train.seat[i];
		}
		os << '\n';

		
		rfile.seekg(train.route_pos, std::ios::beg);
		Station * s_array = new Station[train.station_num];          // s_array: station_array
		rfile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);

		tpfile.seekg(train.ticket_price_pos, std::ios::beg);
		int tp_num = train.seat_num * train.station_num;           // tp_num: ticket_price_num 
		double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
		tpfile.read(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);

		for (int i = 0; i < train.station_num; ++i) {
			os << s_array[i].loc << ' ' << s_array[i].arrive << ' '
				<< s_array[i].depart << ' ' << s_array[i].stop;
			for (int j = 0; j < train.seat_num; ++j) {
				os << ' ' << "￥" << tp_array[i + j * train.station_num];
			}
			os << '\n';
		}

		delete[] s_array;
		delete[] tp_array;

		return 1;
	}
	
	int add_train(const TrainID & train_id, std::istream & is = std::cin, std::ostream & os = std::cout) {

		Train train;
		train.id = train_id;
		is >> train.name >> train.catalog >> train.station_num >> train.seat_num;
		//if ((train_record.find(train.id)).station_num != 0) {
		if (train_record.count(train.id) == 1) {
			for (int i = 0; i < train.seat_num; ++i) {
				is >> train.seat[i];
			}
			Station station;
			double ticket_price;
			for (int i = 0; i < train.station_num; ++i) {
				is >> station.loc >> station.arrive >> station.depart >> station.stop;
				char ch;
				for (int j = 0; j < train.seat_num; ++j) {
					//is >> ch >> ticket_price;
					is >> ch >> ch >> ch >> ticket_price;
				}
			}
			return 0;
		}
		for (int i = 0; i < train.seat_num; ++i) {
			is >> train.seat[i];
		}

		Station * s_array = new Station[train.station_num];          // s_array: station_array
		int tp_num = train.seat_num * train.station_num;           // tp_num: ticket_price_num 
		double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
		
		for (int i = 0; i < train.station_num; ++i) {
			is >> s_array[i].loc >> s_array[i].arrive >> s_array[i].depart >> s_array[i].stop;
			if (i == 0) {
				s_array[i].day = 0;
			}
			else {
				bool flag;
				if (s_array[i - 1].arrive == "xx:xx") {
					flag = s_array[i].arrive > s_array[i - 1].depart;
				}
				else {
					flag = s_array[i].arrive > s_array[i - 1].arrive;
				}
				if (flag) {
					s_array[i].day = s_array[i - 1].day;
				}
				else {
					s_array[i].day = s_array[i - 1].day + 1;
				}
			}
			char ch;
			for (int j = 0; j < train.seat_num; ++j) {
				//is >> ch >> tp_array[i + j * train.station_num];
				is >> ch >> ch >> ch >> tp_array[i + j * train.station_num];
			}
			BindKey bind_key(s_array[i].loc, train.id);
			BindValue bind_value(i, train.catalog);
			station_record.insert(bind_key, bind_value);
		}

		rfile.seekp(0, std::ios::end);
		train.route_pos = rfile.tellp();
		rfile.write(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);
		
		tpfile.seekp(0, std::ios::end);
		train.ticket_price_pos = tpfile.tellp();
		tpfile.write(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);
		
		delete[] s_array;
		delete[] tp_array;
		
		int day_num = (end_date - start_date) + 1;
		int tl_num = day_num * train.seat_num * train.station_num;         // tl_num: ticket_left_num
		
		int * tl_array = new int[tl_num];                                    // tl_array: ticket_left_array
		for (int i = 0; i < tl_num; ++i) {
			tl_array[i] = 2000;
		}

		tlfile.seekp(0, std::ios::end);
		train.ticket_left_pos = tlfile.tellp();
		tlfile.write(reinterpret_cast<char *> (tl_array), sizeof(int) * tl_num);

		delete[] tl_array;
		
		train_record.insert(train.id, train);
		
		return 1;
	}
	
	int delete_train(TrainID & train_id, std::istream & is = std::cin, std::ostream & os = std::cout) {
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		if (train.open == 1) return 0;
		if (train.sale == 1) return 0;
	
		rfile.seekg(train.route_pos, std::ios::beg);
		Station * s_array = new Station[train.station_num];           //s_array: station_array
		rfile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);
		
		for (int i = 0; i < train.station_num; ++i) {
			BindKey bind_key(s_array[i].loc, train.id);
			station_record.remove(bind_key);
		}

		delete[] s_array;
		
		train_record.remove(train_id);
		
		return 1;
	}

	int modify_train(std::istream & is = std::cin, std::ostream & os = std::cout) {
		TrainID train_id;
		is >> train_id;
		int flag = delete_train(train_id, is, os);
		if (flag == 0) {
			Train train;
			is >> train.name >> train.catalog >> train.station_num >> train.seat_num;
			for (int i = 0; i < train.seat_num; ++i) {
				is >> train.seat[i];
			}
			Station station;
			double ticket_price;
			for (int i = 0; i < train.station_num; ++i) {
				is >> station.loc >> station.arrive >> station.depart >> station.stop;
				char ch;
				for (int j = 0; j < train.seat_num; ++j) {
					//is >> ch >> ticket_price;
					is >> ch >> ch >> ch >> ticket_price;
				}
			}
			return 0;
		}
		return add_train(train_id, is, os);
	}

	int query_ticket(std::istream & is = std::cin, std::ostream & os = std::cout) {
		Location loc1, loc2;
		Date date;
		CatalogList catalog_list;
		is >> loc1 >> loc2 >> date >> catalog_list;
		if (date < start_date || date > end_date || loc1 == loc2) {
			return -1;
		}
		
		BindKey bind_key1(loc1), bind_key2(loc2);
		sjtu::vector<sjtu::pair<BindKey, BindValue> > p_array1, p_array2;       // p_array: array of train_id that pass the station
		auto same_loc = [](const BindKey & bk1, const BindKey & bk2)->bool { return bk1.loc < bk2.loc; };
		station_record.search(p_array1, bind_key1, same_loc);
		station_record.search(p_array2, bind_key2, same_loc);

		struct Choice {
			Train train;
			int from;     // number of start station
			int to;       // number of end station
			Choice(const Train & _train, int _from, int _to) : train(_train), from(_from), to(_to) {}
		};

		sjtu::vector<Choice> c_array;                                        // t_array: choice which may statisfy condition
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
			int s1_no = (*iter1).second.station_no, s2_no = (*iter2).second.station_no;
			char catalog = (*iter1).second.catalog;
			if (in_catalog_list(catalog, catalog_list)) {
				if (s1_no >= s2_no) {
					if (s2_no == 0) {
						Train train = train_record.find(id2);
						if (train.station_num == 0 || train.open == 0) {
							++iter1;
							++iter2;
							continue;
						}
						rfile.seekg(train.route_pos + sizeof(Station) * (train.station_num - 1), std::ios::beg);
						Station terminal;
						rfile.read(reinterpret_cast<char *> (&terminal), sizeof(Station));
						if (terminal.loc == loc2 && s1_no < train.station_num - 1) {
							s2_no = train.station_num - 1;
						}
						else {
							++iter1;
							++iter2;
							continue;
						}
					}
					else {
						++iter1;
						++iter2;
						continue;
					}
				}
				Train train = train_record.find(id1);
				if (train.station_num != 0 && train.open == 1) {
					Choice choice(train, s1_no, s2_no);
					c_array.push_back(choice);
				}
			}
			++iter1;
			++iter2;
		}

		os << c_array.size() << '\n';
		
		for (auto iter = c_array.begin(); iter != c_array.end(); ++iter) {
			query_ticket((*iter).train, date, (*iter).from, (*iter).to, is, os);
		}

		return 1;
	}

	int query_transfer(std::istream & is = std::cin, std::ostream & os = std::cout) {
		Location loc1, loc2;
		Date date;
		CatalogList catalog_list;
		is >> loc1 >> loc2 >> date >> catalog_list;
		if (date < start_date || date > end_date || loc1 == loc2) {
			return -1;
		}

		BindKey bind_key1(loc1), bind_key2(loc2);
		sjtu::vector<sjtu::pair<BindKey, BindValue> > p_array1, p_array2;       // p_array: array of train_id that pass the station
		auto same_loc = [](const BindKey & bk1, const BindKey & bk2)->bool { return bk1.loc < bk2.loc; };
		station_record.search(p_array1, bind_key1, same_loc);
		station_record.search(p_array2, bind_key2, same_loc);
	
		sjtu::vector<HalfRoute> ft_array, st_array;         // ft_array: first_train_array   st_array: second_train_array  
		for (int i = 0; i < p_array1.size(); ++i) {
			TrainID train_id = p_array1[i].first.train_id;
			int s_no = p_array1[i].second.station_no;
			char catalog = p_array1[i].second.catalog;
			if (in_catalog_list(catalog, catalog_list) == false) {
				continue;
			}
			Train train = train_record.find(train_id);
			if (train.station_num == 0 || train.open == 0) {
				continue;
			}
			HalfRoute first_train(train, s_no);
			first_train.station_list = new Station[train.station_num];
			rfile.seekg(train.route_pos, std::ios::beg);
			rfile.read(reinterpret_cast<char *> (first_train.station_list), sizeof(Station) * train.station_num);
			ft_array.push_back(first_train);
		}
		for (int i = 0; i < p_array2.size(); ++i) {
			TrainID train_id = p_array2[i].first.train_id;
			int s_no = p_array2[i].second.station_no;
			char catalog = p_array2[i].second.catalog;
			if (in_catalog_list(catalog, catalog_list) == false) {
				continue;
			}
			Train train = train_record.find(train_id);
			if (train.station_num == 0 || train.open == 0) {
				continue;
			}
			HalfRoute second_train(train, s_no);
			second_train.station_list = new Station[train.station_num];
			rfile.seekg(train.route_pos, std::ios::beg);
			rfile.read(reinterpret_cast<char *> (second_train.station_list), sizeof(Station) * train.station_num);
			st_array.push_back(second_train);
		}

		int ft_no = -1, st_no = -1;
		int ft_hub = -1, st_hub = -1;
		int min_time_cost = 100000000;
		for (int i = 0; i < ft_array.size(); ++i) {
			for (int j = 0; j < st_array.size(); ++j) {
				Time depart = ft_array[i].station_list[ft_array[i].station_no].depart,
					arrive = st_array[j].station_list[st_array[j].station_no].arrive;
				if (ft_array[i].train.id == st_array[j].train.id || depart == "xx:xx" || arrive == "xx:xx" || arrive <= depart) {
					continue;
				}
				int hub1, hub2;
				int time_cost = arrive - depart;
				if (time_cost < min_time_cost) {
					if (make_transfer(ft_array[i], st_array[j], hub1, hub2)) {				
						min_time_cost = time_cost;
						ft_no = i;
						st_no = j;
						ft_hub = hub1;
						st_hub = hub2;
					}
				}
			}
		}

		if (min_time_cost != 100000000) {
			show_ticket(ft_array[ft_no].train, ft_array[ft_no].station_no, ft_hub, ft_array[ft_no].station_list, date, is, os);
			show_ticket(st_array[st_no].train, st_hub, st_array[st_no].station_no, st_array[st_no].station_list, date, is, os);
			
		}

		for (int i = 0; i < ft_array.size(); ++i) {
			delete[] ft_array[i].station_list;
		}
		for (int i = 0; i < st_array.size(); ++i) {
			delete[] st_array[i].station_list;
		}
		if (min_time_cost == 100000000) return -1;
		else return 1;
	}

	int buy_ticket(const UserID & user_id, std::istream & is = std::cin, std::ostream & os = std::cout) {
		int num;
		TrainID train_id;
		Location loc1, loc2;
		Date date;
		Seat seat_kind;
		is >> num >> train_id >> loc1 >> loc2 >> date >> seat_kind;

		if (date < start_date || date > end_date || loc1 == loc2) {
			return 0;
		}
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		if (train.open == 0) return 0;

		Date train_date = date;

		Station * s_array = new Station[train.station_num];          // s_array: station_array
		rfile.seekg(train.route_pos, std::ios::beg);
		rfile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);

		int s1_no, s2_no;
		for (int i = 0; i < train.station_num; ++i) {
			if (s_array[i].loc == loc1) {
				s1_no = i;
				break;
			}
		}
		for (int i = 0; i < train.station_num; ++i) {
			if (s_array[i].loc == loc2) {
				s2_no = i;
				break;
			}
		}
		if (s1_no >= s2_no) {
			delete[] s_array;
			return 0;
		}

		int seat_no = -1;
		for (int i = 0; i < train.seat_num; ++i) {
			if (train.seat[i] == seat_kind) {
				seat_no = i;
				break;
			}
		}
		if (seat_no == -1) {
			delete[] s_array;
			return 0;
		}
		int day_no = date - start_date;
		int * tl_array = new int[train.station_num];                                    // tl_array: ticket_left_array
		tlfile.seekg(train.ticket_left_pos + (day_no * train.seat_num * train.station_num  
			+ seat_no * train.station_num) * sizeof(int), std::ios::beg);
		tlfile.read(reinterpret_cast<char *> (tl_array), sizeof(int) * train.station_num);

		int ticket_left = 2000;
		for (int i = s1_no + 1; i <= s2_no; ++i) {
			if (tl_array[i] < ticket_left) {
				ticket_left = tl_array[i];
			}
		}
		if (ticket_left < num) {
			delete[] s_array;
			delete[] tl_array;
			return 0;
		}
		for (int i = s1_no + 1; i <= s2_no; ++i) {
			tl_array[i] -= num;
		}
		
		tlfile.seekp(train.ticket_left_pos + (day_no * train.seat_num * train.station_num 
			+ seat_no * train.station_num) * sizeof(int), std::ios::beg);
		tlfile.write(reinterpret_cast<char *> (tl_array), sizeof(int) * train.station_num);

		if (train.sale == 0) {
			train.sale = 1;
		}
		train_record.set(train_id, train);

		TicketKey ticket_key(user_id, date, train.catalog, train.id, loc1, loc2);
		TicketValue ticket_value = order_record.find(ticket_key);
		if (ticket_value.depart == Time("xx:xx")) {
			ticket_value.day_from = s_array[s1_no].day;
			ticket_value.day_to = s_array[s2_no].day;
			ticket_value.depart = s_array[s1_no].depart;
			ticket_value.arrive = s_array[s2_no].arrive;
			for (int i = 0; i < train.seat_num; ++i) {
				ticket_value.seat[i] = train.seat[i];
			}

			tpfile.seekg(train.ticket_price_pos, std::ios::beg);
			int tp_num = train.seat_num * train.station_num;             // tp_num: ticket_price_num 
			double * tp_array = new double[tp_num];                      // tp_array: ticket_price_array
			tpfile.read(reinterpret_cast<char *> (tp_array), sizeof(double) * tp_num);
			
			for (int i = 0; i < train.seat_num; ++i) {
				ticket_value.price[i] = 0;
				ticket_value.num[i] = 0;
				for (int j = s1_no + 1; j <= s2_no; ++j) {
					ticket_value.price[i] += tp_array[j + i * train.station_num];
				}
			}
			delete[] tp_array;
			ticket_value.num[seat_no] += num;

			order_record.insert(ticket_key, ticket_value);
		}
		else {
			ticket_value.num[seat_no] += num;
			order_record.set(ticket_key, ticket_value);
		}

		delete[] tl_array;
		delete[] s_array;

		return 1;
	}

	int query_order(const UserID & user_id, std::istream & is = std::cin, std::ostream & os = std::cout) {
		Date date;
		CatalogList catalog_list;
		is >> date >> catalog_list;
		if (date < start_date || date > end_date) {
			return -1;
		}
		sjtu::vector<sjtu::vector<sjtu::pair<TicketKey, TicketValue> > > t_array;     // t_array: ticket_array
		int i = 0;
		while (catalog_list[i] != '\0') {
			TicketKey ticket_key(user_id, date, catalog_list[i]);
			sjtu::vector<sjtu::pair<TicketKey, TicketValue> > tc_array;               // tc_array: certain_catalog_ticket_array
			auto meet_condition = [](const TicketKey & tk1, const TicketKey & tk2)->bool { 
				if (tk1.user_id < tk2.user_id) return 1;
				if (tk1.user_id > tk2.user_id) return 0;
				if (tk1.date < tk2.date) return 1;
				if (tk1.date > tk2.date) return 0;
				return tk1.catalog < tk2.catalog;
			};
			order_record.search(tc_array, ticket_key, meet_condition);
			if (!tc_array.empty()) {
				t_array.push_back(tc_array);
			}
			++i;
		}
		int list_num = 0;
		for (int i = 0; i < t_array.size(); ++i) {
			list_num += t_array[i].size();
		}
		os << list_num << '\n';
		for (int i = 0; i < t_array.size(); ++i) {
			for (int j = 0; j < t_array[i].size(); ++j) {
				Date date1 = date, date2 = date;
				if (t_array[i][j].second.day_from == 1) ++date1;
				if (t_array[i][j].second.day_to == 1) ++date2;
				os << t_array[i][j].first.train_id << ' '
					<< t_array[i][j].first.from << ' ' << date1 << ' ' << t_array[i][j].second.depart << ' '
					<< t_array[i][j].first.to << ' ' << date2 << ' ' << t_array[i][j].second.arrive;
				int k = 0;
				while (t_array[i][j].second.num[k] != -1) {
					os << ' ' << t_array[i][j].second.seat[k] << ' ' 
						<< t_array[i][j].second.num[k] << ' ' 
						<< t_array[i][j].second.price[k];
					++k;
				}
				os << '\n';
			}
		}
		return 1;
	}

	int refund_ticket(const UserID & user_id, std::istream & is = std::cin, std::ostream & os = std::cout) {
		int num;
		TrainID train_id;
		Location loc1, loc2;
		Date date;
		Seat seat_kind;
		is >> num >> train_id >> loc1 >> loc2 >> date >> seat_kind;

		if (date < start_date || date > end_date) {
			return 0;
		}
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
	
		int seat_no;
		for (int i = 0; i < train.seat_num; ++i) {
			if (seat_kind == train.seat[i]) {
				seat_no = i;
				break;
			}
		}

		TicketKey ticket_key(user_id, date, train.catalog, train.id, loc1, loc2);
		TicketValue ticket_value = order_record.find(ticket_key);
		if (ticket_value.num[seat_no] < num) {
			return 0;
		}
		ticket_value.num[seat_no] -= num;
		bool flag = false;
		for (int i = 0; i < train.seat_num; ++i) {
			if (ticket_value.num[i] > 0) {
				flag = true;
				break;
			}
		}
		if (flag == true) {
			order_record.set(ticket_key, ticket_value);
		}
		else {
			order_record.remove(ticket_key);
		}

		int s1_no, s2_no;
		Station * s_array = new Station[train.station_num];          // s_array: station_array
		rfile.seekg(train.route_pos, std::ios::beg);
		rfile.read(reinterpret_cast<char *> (s_array), sizeof(Station) * train.station_num);
		for (int i = 0; i < train.station_num; ++i) {
			if (s_array[i].loc == loc1) {
				s1_no = i;
				break;
			}
		}
		for (int i = 0; i < train.station_num; ++i) {
			if (s_array[i].loc == loc2) {
				s2_no = i;
				break;
			}
		}
		delete[] s_array;

		int day_no = date - start_date;
		int * tl_array = new int[train.station_num];                                    // tl_array: ticket_left_array
		tlfile.seekg(train.ticket_left_pos + (day_no * train.seat_num * train.station_num
			+ seat_no * train.station_num) * sizeof(int), std::ios::beg);
		tlfile.read(reinterpret_cast<char *> (tl_array), sizeof(int) * train.station_num);
		for (int i = s1_no + 1; i <= s2_no; ++i) {
			tl_array[i] += num;
		}
		tlfile.seekp(train.ticket_left_pos + (day_no * train.seat_num * train.station_num 
			+ seat_no * train.station_num) * sizeof(int), std::ios::beg);
		tlfile.write(reinterpret_cast<char *> (tl_array), sizeof(int) * train.station_num);
		delete[] tl_array;

		return 1;
	}

	
	void list_station(std::istream & is = std::cin, std::ostream & os = std::cout) {
		rfile.seekg(0, std::ios::end);
		long route_file_end = rfile.tellg();
		rfile.seekg(0, std::ios::beg);
		sjtu::map<Location, int> station_map;
		Station station;
		for (int i = 0; i < route_file_end; i += sizeof(Station)) {
			rfile.read(reinterpret_cast<char *> (&station), sizeof(Station));
			station_map[station.loc] = 1;
		}
		os << station_map.size() << '\n';
		for (auto iter = station_map.begin(); iter != station_map.end(); ++iter) {
			os << (*iter).first << ' ';
		}
		if (!station_map.empty()) {
			os << '\n';
		}
	}
	
	void list_unsale_train(std::istream & is = std::cin, std::ostream & os = std::cout) {
		auto list_unsale = [&](const TrainID & train_id, const Train & train)->void {
			if (train.open == 0) {
				os << train_id << '\n';
			}
		};
		train_record.traverse(list_unsale);
	}
};


