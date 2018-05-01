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

	bool operator==(const String & other) const {
		int len1, len2;
		len1 = strlen(ch);
		len2 = strlen(other.ch);
		if (len1 != len2) return 0;
		for (int i = 0; i < len1; ++i) {
			if (ch[i] != other.ch[i])
				return 0;
		}
		return 1;
	}

	char & operator[](size_t index) {
		return ch[index];
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

