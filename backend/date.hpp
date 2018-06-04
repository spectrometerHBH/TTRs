#pragma once

#include <iostream>

const int _date_mon[13] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

class Date {
private:
	int year;
	int month;
	int day;
	
	bool is_leap_year(int y) {
		return ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0));
	}

	static int mon[13];

public:
	Date(int y = 0, int m = 0, int d = 0) : year(y), month(m), day(d) {}
	
	Date operator++() {
		int m = _date_mon[month];
		if (is_leap_year(year) && month == 2) {
			m = 29;
		}
		if (day < m) {
			++day;
		}
		else {
			day = 1;
			if (month < 12) {
				++month;
			}
			else {
				month = 1;
				++year;
			}
		}
		return *this;
	}

	bool operator==(const Date & other) {
		return (year == other.year && month == other.month && day == other.day);
	}

	bool operator!=(const Date & other) {
		return !(this->operator==(other));
	}

	friend bool operator<(const Date & d1, const Date & d2) {
		if (d1.year < d2.year) return 1;
		if (d1.year > d2.year) return 0;
		if (d1.month < d2.month) return 1;
		if (d1.month > d2.month) return 0;
		if (d1.day < d2.day) return 1;
		if (d1.day > d2.day) return 0;
		return 0;
	}

	friend bool operator>(const Date & d1, const Date & d2) {
		return operator<(d2, d1);
	}

	friend bool operator>=(const Date & d1, const Date & d2) {
		return !(operator<(d1, d2));
	}

	friend bool operator<=(const Date & d1, const Date & d2) {
		return !(operator>(d1, d2));
	}

	friend std::istream & operator>>(std::istream & is, Date & date) {
		char ch;
		is >> date.year >> ch >> date.month >> ch >> date.day;
		return is;
	}
	
	friend std::ostream & operator<<(std::ostream & os, const Date & date) {
		os << date.year << '-';
		if (date.month < 10) os << 0;
		os << date.month << '-';
		if (date.day < 10) os << 0;
		os << date.day;
		return os;
	}

	friend int operator-(const Date & d1, const Date & d2) {
		/*
		if (d2 < d1) {
			return -(operator-(d2, d1));
		}
		else {
			int n1, n2;
			int y = d1.year;
			while (y != d2.year) {
				if (d1.is_leap_year(y)) {
					n2 += 366;
				}
				else {
					n2 += 365;
				}
				++y;
			}
		}
		*/
		if (d1 < d2) {
			return -(operator-(d2, d1));
		}
		Date d = d2;
		int dis = 0;
		while (d != d1) {
			++d;
			++dis;
		}
		return dis;
	}
};


const Date start_date(2018, 6, 1), end_date(2018, 7, 1);

