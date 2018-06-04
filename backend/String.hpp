#pragma once

#include <cstring>
#include <iostream>

template<int N>
class String {
private:
	char ch[N + 1];

public:
	String() { ch[0] = '\0'; }

	String(const char * cp) {
		if (cp != nullptr) {
			int i = 0;
			while (cp[i] != '\0' && i < N) {
				ch[i] = cp[i];
				++i;
			}
			ch[i] = '\0';
		}
		else {
			ch[0] = '\0';
		}
	}

	String(const String & other) {
		int i = 0;
		while (other.ch[i] != '\0' && i < N) {
			ch[i] = other.ch[i];
			++i;
		}
		ch[i] = '\0';
	}

	String operator=(const char * cp) {
		if (cp != nullptr) {
			int i = 0;
			while (cp[i] != '\0' && i < N) {
				ch[i] = cp[i];
				++i;
			}
			ch[i] = '\0';
		}
		return *this;
	}

	String operator=(const String & other) {
		if (&other == this) return *this;
		int i = 0;
		while (other.ch[i] != '\0' && i < N) {
			ch[i] = other.ch[i];
			++i;
		}
		ch[i] = '\0';
		return *this;
	}

	/*
	String operator+(const char * ap) {
		String sum(*this);
		if (ap != nullptr) {
			int i = 0, j = 0;
			while (sum[i] != '\0') ++i;
			while (ap[j] != '\0' && i + j < N) {
				sum[i + j] = ap[j];
				++j;
			}
			sum[i + j] = '\0';
		}
		return sum;
	}

	template<int M>
	String operator+(const String<M> & ap) {
		String sum(*this);
		int i = 0, j = 0;
		while (sum[i] != '\0') ++i;
		while (ap[j] != '\0' && i + j < N) {
			sum[i + j] = ap[j];
			++j;
		}
		sum[i + j] = '\0';
		return sum;
	}
	*/

	bool operator<(const String & other) const {
		int len1, len2, len, i;
		len1 = strlen(ch);
		len2 = strlen(other.ch);
		len = (len1 < len2) ? len1 : len2;
		for (i = 0; i < len; ++i) {
			if (ch[i] < other.ch[i]) return 1;
			if (ch[i] > other.ch[i]) return 0;
		}
		if (len1 < len2) return 1;
		else return 0;
	}

	bool operator>=(const String & other) const {
		return !(this->operator<(other));
	}

	bool operator>(const String & other) const {
		return other.operator<(*this);
	}

	bool operator<=(const String & other) const {
		return !(this->operator>(other));
	}

	bool operator==(const String & other) const {
		return this->operator==(other.ch);
	}

	bool operator!=(const String & other) const {
		return !(this->operator==(other));
	}

	bool operator==(const char * other) const {
		if (other == nullptr) {
			return 0;
		}
		int len1, len2;
		len1 = strlen(ch);
		len2 = strlen(other);
		if (len1 != len2) return 0;
		for (int i = 0; i < len1; ++i) {
			if (ch[i] != other[i])
				return 0;
		}
		return 1;
	}

	bool operator!=(const char * other) const {
		return !(this->operator==(other));
	}

	char & operator[](size_t index) {
		return ch[index];
	}

	char * getAddress() {
		char * p = ch;
		return p;
	}

	const char & operator[](size_t index) const {
		return ch[index];
	}


	friend std::ostream & operator<<(std::ostream & os, const String & other) {
		os << other.ch;
		return os;
	}

	friend std::istream & operator>>(std::istream & is, String & other) {
		is >> other.ch;
		return is;
	}
};

template<int L1, int L2> 
String<L1 + L2> operator+(const String<L1> & s1, const String<L2> & s2) {
	String<L1 + L2> sum;
	for (int i = 0; i < L1; ++i) {
		sum[i] = s1[i];
	}
	for (int i = 0; i < L2; ++i) {
		sum[L1 + i] = s2[i];
	}
	sum[L1 + L2] = '\0';
	return sum;
}

template<int N>
String<N> IntToString(int x) {
	String<N> num;
	int i = N - 1;
	while (x != 0 && i >= 0) {
		num[i] = (char)(x % 10 + '0');
		x /= 10;
		i--;
	}
	while (i >= 0) {
		num[i] = '0';
		--i;
	}
	num[N] = '\0';
	return num;
}


#ifndef SIGNAL
#define SIGNAL
//about user
typedef String<40> UserName;
typedef String<20> Password;
typedef String<20> Email;
typedef String<20> Phone;
typedef long long UserID;

// about train
typedef String<5> Time;
typedef String<10> CatalogList;
typedef char Catalog;
typedef String<20> Location;
typedef String<20> TrainID;
typedef String<40> TrainName;
typedef String<20> Seat;
#endif // !SIGNAL

inline int turn_to_minute(const Time & time) {
	int hour = ((int)(time[0] - '0')) * 10 + (int)(time[1] - '0');
	int min = ((int)(time[3] - '0')) * 10 + (int)(time[4] - '0');
	return hour * 60 + min;
}

inline int operator-(const Time & t1, const Time & t2) {
	return turn_to_minute(t1) - turn_to_minute(t2);
}