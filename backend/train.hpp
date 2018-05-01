#include <iostream>
#include "bplus_tree\exceptions.h"
#include "String.hpp"
#include "bplus_tree\bptree.hpp"
#include "date.hpp"

class TrainManager {
private:
	struct Train{
		TrainID id;
		TrainName name;
		Catalog catalog;
		int station_num;
		int ticket_num;
		long route_file_pos;
		long ticket_file_pos;
		Ticket ticket[10];
		bool open;
		Train() : station_num(0), ticket_num(0), open(false) {}
	};

	struct Station {
		Location loc;
		Time arrive;
		Time depart;
		Time stop;
	};

	typedef bptree<TrainID, Train> TrainRecord;

	TrainRecord train_record;
	String<20> route_file_name;
	String<20> ticket_file_name;

public:
	TrainManager() : train_record("train_record", "index_train_record"), 
		route_file_name("route_record"), ticket_file_name("ticket_record") {
		train_record.init();
		std::ofstream out;
		out.open(route_file_name.getAddress());
		if (!out) {
			std::cerr << "create file error haha\n";
		}
		out.close();
		out.open(ticket_file_name.getAddress());
		if (!out) {
			std::cerr << "create file error haha\n";
		}
		out.close();
	}

	int add_train() {
		Train new_train;
		std::cin >> new_train.id >> new_train.name >> new_train.catalog
			>> new_train.station_num >> new_train.ticket_num;
		for (int i = 0; i < new_train.ticket_num; ++i) {
			std::cin >> new_train.ticket[i];
		}
		new_train.open = false;
		std::fstream iofile;
		iofile.open(route_file_name.getAddress());
		if (!iofile) {
			std::cerr << "create file error haha\n";
			return 0;
		}
		iofile.seekp(std::ios::end);
		new_train.route_file_pos = iofile.tellp();
		Station station;
		double * ticket_price = new double[new_train.ticket_num];
		for (int i = 0; i < new_train.station_num; ++i) {
			std::cin >> station.loc >> station.arrive >> station.depart >> station.stop;
			iofile.write(reinterpret_cast<char *> (&station), sizeof(Station));
			char ch;
			for (int j = 0; j < new_train.ticket_num; ++j) {
				std::cin >> ch >> ticket_price[j];
			}
			iofile.write(reinterpret_cast<char *> (ticket_price), sizeof(double) * new_train.ticket_num);
		}
		delete[] ticket_price;
		iofile.close();
		iofile.open(ticket_file_name.getAddress());
		if (!iofile) {
			std::cerr << "create file error\n";
			return 0;
		}
		iofile.seekp(std::ios::end);
		new_train.ticket_file_pos = iofile.tellp();
		int day_num = (end_date - start_date) + 1;
		int ticket_sum = new_train.ticket_num * day_num * new_train.station_num;
		int * ticket_num = new int[ticket_sum];
		for (int i = 0; i < ticket_sum; ++i) {
			ticket_num[i] = 2000;
		}
		iofile.write(reinterpret_cast<char *> (ticket_num), sizeof(int) * ticket_sum);
		delete[] ticket_num;
		iofile.close();
		train_record.insert(new_train.id, new_train);
		return 1;
	}

	int safe_train(TrainID & train_id) {
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		if (train.open == true) return 0;
		train.open = true;
		return 1;
	}

	int query_train(TrainID & train_id) {
		Train train = train_record.find(train_id);
		if (train.station_num == 0) return 0;
		//if (train.open == false) return 0;
		std::cout << train.id << ' ' << train.name << ' ' << train.station_num << ' '
			<< train.ticket_num << ' ';
		for (int i = 0; i < train.ticket_num; ++i) {
			std::cout << train.ticket[i] << ' ';
		}
		std::cout << '\n';
		std::fstream iofile;
		iofile.open(route_file_name.getAddress());
		if (!iofile) {
			std::cerr << "create file error\n";
			return 0;
		}
		iofile.seekg(train.route_file_pos);
		Station station;
		double * ticket_price = new double[train.ticket_num];
		for (int i = 0; i < train.station_num; ++i) {
			iofile.read(reinterpret_cast<char *> (&station), sizeof(Station));
			std::cout << station.loc << ' ' << station.arrive << ' ' 
				<< station.depart << ' ' << station.stop << ' ';
			iofile.read(reinterpret_cast<char *> (ticket_price), sizeof(double) * train.ticket_num);
			for (int i = 0; i < train.ticket_num; ++i) {
				std::cout << '$' << ticket_price[i] << ' ';
			}
			std::cout << '\n';
		}
		delete[] ticket_price;
		iofile.close();
		return 1;
	}

};