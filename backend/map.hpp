/**
 * implement a container like std::map
 AVL
 */
#ifndef SJTU_MAP_HPP
#define SJTU_MAP_HPP

// only for std::less<T>
#include <functional>
#include <cstddef>
#include <iostream>
#include <cstdlib>
#include "pair.hpp"
#include "exceptions.h"
using std::cout;
using std::endl;


namespace sjtu {

template<
	class Key,
	class T,
	class Compare = std::less<Key>
> class map {
friend class iterator;
public:
	typedef pair<const Key, T> value_type;
private:

	inline size_t max(size_t a, size_t b) {
		return (a > b ? a : b);
	}
	Compare cmp = Compare();
	struct list_node;
	struct node {
		node *father = nullptr, *left = nullptr, *right = nullptr;
		//*prev, *next;
		size_t h = 1;
		//value_type * data;
		list_node * lnode = nullptr;

	};

	struct list_node {
		list_node * prev = nullptr, *next = nullptr;
		value_type * data = nullptr;
		node * tnode = nullptr;
		list_node() = default;
		list_node( const value_type &v) {
			data = new value_type(v);
		}
		//list_node(const list_node &other):prev(other.prev), next(other.next), tnode(other->tnode)
		~list_node() {
			delete data;
		}
	};

	node *root = nullptr;
	list_node *head, *tail;
	size_t theSize;
	size_t height(node *p) {
		if (p) return p->h;
		else return 0;
	}

	node * replicate(node *src) {
		if (!src) {
			return nullptr;
		}
		list_node *ln = new list_node(*(src->lnode->data));
		node *r = new node();
		
		ln->tnode = r;
		r->lnode = ln;
		
		if (src->left) {
			r->left = replicate(src->left);
			r->left->father = r;
		}
		
		ln->prev = tail->prev;
		tail->prev->next = ln;
		tail->prev = ln;
		ln->next = tail;

		if (src->right) {
			r->right = replicate(src->right);
			r->right->father = r;
		}
		return r;
	}


	void output(node *r, int level) {
		if (!r) return;
		int i;
		for (i = 0; i < level; ++i) cout << '\t';
		cout << "| " << r->lnode->data->first << ":" <<r->lnode->data->second <<' '<<r->h<< endl;
		if (r->left) output(r->left, level + 1);
		if (r->right) output(r->right, level + 1);
	}

	void swap(node *a, node*b) {
		//value_type *tmp = a->data;
		list_node *p = a->lnode;
		a->lnode->tnode = b;
		b->lnode->tnode = a;
		a->lnode = b->lnode;
		b->lnode = p;

	}

	void ll(node *p) {
		node *tmp = p->left->right;
		swap(p, p->left);
		if (p->right)p->right->father = p->left;
		p->left->right = p->right;

		p->right = p->left;
		p->left = p->left->left;
		if (p->left) p->left->father = p;

		p->right->left = tmp;
		if (tmp) tmp->father = p->right;


		p->right->h = max(height(p->right->left), height(p->right->right)) + 1;
		p->h = max(height(p->left), height(p->right)) + 1;
	}
	void rr(node *p) {
		//cout << p->right->right << endl;
		node *tmp = p->right->left;
		swap(p, p->right);
		//cout << p->right->right << endl;
		if (p->left) p->left->father = p->right;

		p->right->left = p->left;
		//cout << p->right->right << endl;

		p->left = p->right;
		p->right = p->right->right;
		if (p->right) p->right->father = p;

		p->left->right = tmp;
		if (tmp) tmp->father = p->left;

		
		p->left->h = max(height(p->left->left), height(p->left->right)) + 1;
		p->h = max(height(p->left), height(p->right)) + 1;
	}
	void lr(node *p) {
		rr(p->left);
		ll(p);
	}
	void rl(node *p) {
		ll(p->right);
		rr(p);
	}



	bool insert(node *&r, node * f, const Key &k, const T &v) { //0 fail 1 success
		if (!r) {
			r = new node;
			r->father = f;
			list_node *l = new list_node(value_type(k, v));
			r->lnode = l;
			l->tnode = r;
			++theSize;
			if (!f) {
				head->next = l;
				tail->prev = l;
				l->prev = head;
				l->next = tail;
			}
			else {
				if (r == f->left) {
					f->lnode->prev->next = l;
					l->prev = f->lnode->prev;
					l->next = f->lnode;
					f->lnode->prev = l;
				}
				else {
					f->lnode->next->prev = l;
					l->next = f->lnode->next;
					l->prev = f->lnode;
					f->lnode->next = l;
				}
			}
			return true;
		}
		
		if (cmp(k, r->lnode->data->first)) {
			if (!insert(r->left, r, k, v)) return false;
			r->h = max(height(r->left), height(r->right)) + 1;
			if (height(r->left)  >= 2 + height(r->right)) {
				if (height(r->left->left) > height(r->left->right))
					ll(r);
				else lr(r);
			}
			return true;
		}
		else if (cmp(r->lnode->data->first, k)) {
			if (!insert(r->right, r, k, v)) return false;
			r->h = max(height(r->left), height(r->right)) + 1;
			if (height(r->right)  >= height(r->left) + 2) {
				if (height(r->right->right) > height(r->right->left))
					rr(r);
				else rl(r);
			}
			return true;
		}
		else {
			return false;
		}
	}

	void remove_adjust(node *&r, bool x) { //x=0 É¾×ó x=1 É¾ÓÒ
		r->h = max(height(r->left), height(r->right)) + 1;
		if (x) {
			if (height(r->right) == height(r->left)) {
				if (r != root) remove_adjust(r->father, (r == r->father->right));
				return;
			}
			if (height(r->left) - height(r->right) == 1) return;
			if (height(r->left) - height(r->right) == 2) {
				if (height(r->left->right) > height(r->left->left)) lr(r);
				else ll(r);
				if (r ->father) remove_adjust(r->father, (r == r->father->right));
				return;
			}
		}
		else {
			if (height(r->left) == height(r->right)) {
				if (r->father) remove_adjust(r->father, (r == r->father->right));
				return;
			}
			if (height(r->right) - height(r->left) == 1) return;
			if (height(r->right) - height(r->left) == 2) {
				if (height(r->right->left) > height(r->right->right)) rl(r);
				else rr(r);
				if (r ->father) remove_adjust(r->father, (r == r->father->right));
				return;
			}
		}
	}
	


	void remove(node *r, const Key &k) {
		//cout << r->data.first << ' ' << k << endl;
		if (!r) return;
		if (!(r->left) && !(r->right)) {
			--theSize;
			r->lnode->prev->next = r->lnode->next;
			r->lnode->next->prev = r->lnode->prev;
			//cout << "delete " << k << endl;
			bool x;
			if (r == root) root = nullptr;
			else x = (r->father->right == r);
			node * f = r->father;
			if (f) {
				if (f->left == r) f->left = nullptr;
				else f->right = nullptr;
			}
			delete r->lnode;
			delete r;
			if (root) remove_adjust(f, x);
			return;
		}
		if (cmp(k, r->lnode->data->first)) {
			remove(r->left, k);
		}
		else if (cmp(r->lnode->data->first, k)) {
			remove(r->right, k);
		}
		else {
			if (!(r->right)) {
				swap(r, r->left);
				remove(r->left, k);
			}
			else {
				//node * tmp = r->next;
				//cout << "! ";
				//output();
				node *tmp = r->right;
				while (tmp->left) tmp = tmp->left;
				//cout << " replacement: " << tmp->data->first << endl;
				//cout << "* ";
				swap(r, tmp);
				//output();
				//cout << " replacement: " << tmp->data->first << endl;
				remove(r->right, k);
			}
		}

	}


public:
	/**
	 * the internal type of data.
	 * it should have a default constructor, a copy constructor.
	 * You can use sjtu::map as value_type by typedef.
	 */
	
	/**
	 * see BidirectionalIterator at CppReference for help.
	 *
	 * if there is anything wrong throw invalid_iterator.
	 *     like it = map.begin(); --it;
	 *       or it = map.end(); ++end();
	 */
	class const_iterator;
	class iterator {
	friend class const_iterator;
	private:
		list_node * ptr;
		const map * m;
	public:
		iterator(const map * m = nullptr, list_node *p=nullptr) : 
			ptr(p), m(m) {}
		iterator(const iterator &other) : 
			ptr(other.ptr), m(other.m){}
		/**
		 * return a new iterator which pointer n-next elements
		 *   even if there are not enough elements, just return the answer.
		 * as well as operator-
		 */
		/**
		 * TODO iter++
		 */
		iterator operator++(int) {
			if (ptr == m->tail) throw invalid_iterator();
			iterator old(*this);
			ptr = ptr->next;
			return old;
		}
		/**
		 * TODO ++iter
		 */
		iterator & operator++() {
			if (ptr == m->tail) throw invalid_iterator();
			else ptr = ptr->next;
			return *this;
		}
		/**
		 * TODO iter--
		 */
		iterator operator--(int) {
			if (ptr->prev == m->head) throw invalid_iterator();
			iterator old(*this);
			ptr = ptr->prev;
			return old;
		}
		/**
		 * TODO --iter
		 */
		iterator & operator--() {
			if (ptr->prev == m->head) throw invalid_iterator();
			else ptr = ptr->prev;
			return *this;
		}

		/**
		 * a operator to check whether two iterators are same (pointing to the same memory).
		 */

		value_type & operator*() const {
		    if (ptr == m->tail) throw invalid_iterator();
			return *(ptr->data);
		}
		bool operator==(const iterator &rhs) const {
			return ptr == rhs.ptr;
		}
		bool operator==(const const_iterator &rhs) const {
			return ptr == rhs.ptr;
		}

		/**
		 * some other operator for iterator.
		 */

		bool operator!=(const iterator &rhs) const {
			return ptr != rhs.ptr;
		}
		bool operator!=(const const_iterator &rhs) const {
			return ptr != rhs.ptr;
		}

		/**
		 * for the support of it->first. 
		 * See <http://kelvinh.github.io/blog/2013/11/20/overloading-of-member-access-operator-dash-greater-than-symbol-in-cpp/> for help.
		 */
		value_type* operator->() const noexcept {
			//if (ptr == m->tail) throw invalid_iterator();
			return ptr->data;
		}
		const map * _map() { return m; }
		list_node *_lnode() { return ptr; }
	};
	class const_iterator {
	friend class iterator;
	private:
		list_node * ptr;
		const map * m;
	public:
		const_iterator(const map * m = nullptr, list_node *p = nullptr) :
			ptr(p), m(m) {}
		const_iterator(const const_iterator &other) :
			ptr(other.ptr), m(other.m) {}
		const_iterator(const iterator &other) :
			ptr(other.ptr), m(other.m) {}
		/**
		* return a new iterator which pointer n-next elements
		*   even if there are not enough elements, just return the answer.
		* as well as operator-
		*/
		/**
		* TODO iter++
		*/
		const_iterator operator++(int) {
			if (ptr == m->tail) throw invalid_iterator();
			const_iterator old(*this);
			ptr = ptr->next;
			return old;
		}
		/**
		* TODO ++iter
		*/
		const_iterator & operator++() {
			if (ptr == m->tail) throw invalid_iterator();
			else ptr = ptr->next;
			return *this;
		}
		/**
		* TODO iter--
		*/
		const_iterator operator--(int) {
			if (ptr->prev == m->head) throw invalid_iterator();
			const_iterator old(*this);
			ptr = ptr->prev;
			return old;
		}
		/**
		* TODO --iter
		*/
		const_iterator & operator--() {
			if (ptr->prev == m->head) throw invalid_iterator();
			else ptr = ptr->prev;
			return *this;
		}

		/**
		* a operator to check whether two iterators are same (pointing to the same memory).
		*/

		const value_type & operator*() const {
			if (ptr == m->tail) throw invalid_iterator();
			return *(ptr->data);
		}
		bool operator==(const iterator &rhs) const {
			return ptr == rhs.ptr;
		}
		bool operator==(const const_iterator &rhs) const {
			return ptr == rhs.ptr;
		}

		/**
		* some other operator for iterator.
		*/

		bool operator!=(const iterator &rhs) const {
			return ptr != rhs.ptr;
		}
		bool operator!=(const const_iterator &rhs) const {
			return ptr != rhs.ptr;
		}

		/**
		* for the support of it->first.
		* See <http://kelvinh.github.io/blog/2013/11/20/overloading-of-member-access-operator-dash-greater-than-symbol-in-cpp/> for help.
		*/
		const value_type* operator->() const noexcept {
			//if (ptr == m->tail) throw invalid_iterator();
			return ptr->data;
		}
		const map * _map() { return m; }
		list_node *_lnode() { return ptr; }
	};
	/**
	 * TODO two constructors
	 */
	map() :theSize(0){
		head = (list_node *)malloc(sizeof(list_node));
		head->prev = nullptr;
		tail = (list_node *)malloc(sizeof(list_node));
		tail->next = nullptr;
		head->next = tail;
		tail->prev = head;
	}
	map(const map &other) {
		head = (list_node *)malloc(sizeof(list_node));
		head->prev = nullptr;
		tail = (list_node *)malloc(sizeof(list_node));
		tail->next = nullptr;
		head->next = tail;
		tail->prev = head;
		theSize = other.theSize;
		root = replicate(other.root);
	}
	~map() {
		clear();
		free(head);
		free(tail);
	}
	void insert(const Key&k, const T&v) {
		insert(root, nullptr, k, v);
	}

	void output() {
		output(root, 0);
		list_node * p = head->next;
		while (p != tail) {
			cout << p->data->first << ' ';
			p = p->next;
		}
		cout << endl;
		p = tail->prev;
		while (p != head) {
			cout << p->data->first << ' ';
			p = p->prev;
		}
		cout << endl;
	}

	void remove(const Key&k) {
		remove(root, k);
	}
	/**
	 * TODO assignment operator
	 */
	map & operator=(const map &other) {
		if (&other == this) return *this;
		clear();
		theSize = other.theSize;
		root = replicate(other.root);
		return (*this);
	}
	/**
	 * TODO
	 * access specified element with bounds checking
	 * Returns a reference to the mapped value of the element with key equivalent to key.
	 * If no such element exists, an exception of type `index_out_of_bound'
	 */
	T & at(const Key &key) {
		node *p = root;
		while (p) {
			if (cmp(key, p->lnode->data->first))
				p = p->left;
			else if (cmp(p->lnode->data->first, key))
				p = p->right;
			else return p->lnode->data->second;
		}
		throw index_out_of_bound();
	}
	const T & at(const Key &key) const {
		node *p = root;
		while (p) {
			if (cmp(key, p->lnode->data->first))
				p = p->left;
			else if (cmp(p->lnode->data->first, key))
				p = p->right;
			else return p->lnode->data->second;
		}
		throw index_out_of_bound();
	}
	/**
	 * TODO
	 * access specified element 
	 * Returns a reference to the value that is mapped to a key equivalent to key,
	 *   performing an insertion if such key does not already exist.
	 */
	T & operator[](const Key &key) {
		if (!root) {
			++theSize;
			root = new node();
			list_node * l = new list_node(value_type(key, T()));
			root->lnode = l;
			l->tnode = root;
			l->prev = head;
			head->next = l;
			l->next = tail;
			tail->prev = l;
			return l->data->second;
		}
		node *p = root;
		node *q = root;
		while(p) {
			q = p;
			if (cmp(key, p->lnode->data->first))
				p = p->left;
			else if (cmp(p->lnode->data->first, key))
				p = p->right;
			else return p->lnode->data->second;
		}
		insert(root, nullptr, key, T());
		return this->at(key);
	}
	/**
	 * behave like at() throw index_out_of_bound if such key does not exist.
	 */
	const T & operator[](const Key &key) const {
		node *p = root;
		while(p) {
			if (cmp(key, p->lnode->data->first))
				p = p->left;
			else if (cmp(p->lnode->data->first, key))
				p = p->right;
			else return p->lnode->data->second;
		}
		throw index_out_of_bound();
	}
	/**
	 * return a iterator to the beginning
	 */
	iterator begin() {
		return iterator(this,head->next);
	}
	const_iterator cbegin() const {
		return const_iterator(this, head->next);
	}
	/**
	 * return a iterator to the end
	 * in fact, it returns past-the-end.
	 */
	iterator end() {
		return iterator(this, tail);
	}
	const_iterator cend() const {
		return const_iterator(this, tail);
	}
	/**
	 * checks whether the container is empty
	 * return true if empty, otherwise false.
	 */
	bool empty() const {
		return theSize == 0;
	}
	/**
	 * returns the number of elements.
	 */
	size_t size() const {
		return theSize;
	}
	/**
	 * clears the contents
	 */
	void clear() {
		root = nullptr;
		list_node *p = head->next, *q;
		while (p != tail) {
			q = p->next;
			delete p->tnode;
			delete p;
			p = q;
		}
		head->next = tail;
		tail->prev = head;
		theSize = 0;
	}
	/**
	 * insert an element.
	 * return a pair, the first of the pair is
	 *   the iterator to the new element (or the element that prevented the insertion), 
	 *   the second one is true if insert successfully, or false.
	 */
	pair<iterator, bool> insert(const value_type &value) {
		if (insert(root, nullptr,  value.first, value.second))
		    return pair<iterator, bool>(this->find(value.first), true);
		else 
			return pair<iterator, bool>(this->find(value.first), false);
	}
	/**
	 * erase the element at pos.
	 *
	 * throw if pos pointed to a bad element (pos == this->end() || pos points an element out of this)
	 */
	void erase(iterator pos) {
		if (pos == end() || pos._map()!=this) throw invalid_iterator();
		//remove(pos.addr(), pos.addr()->data->first);
		//cout << "erase:" << pos->first << endl;
		remove(pos._lnode()->tnode, pos->first);
	}
	/**
	 * Returns the number of elements with key 
	 *   that compares equivalent to the specified argument,
	 *   which is either 1 or 0 
	 *     since this container does not allow duplicates.
	 * The default method of check the equivalence is !(a < b || b > a)
	 */
	size_t count(const Key &key) const {
		node *p = root;
		while(p) {
			if (cmp(key, p->lnode->data->first))
				p = p->left;
			else if (cmp(p->lnode->data->first, key))
				p = p->right;
			else return 1;
		}
		return 0;
	}
	/**
	 * Finds an element with key equivalent to key.
	 * key value of the element to search for.
	 * Iterator to an element with key equivalent to key.
	 *   If no such element is found, past-the-end (see end()) iterator is returned.
	 */
	iterator find(const Key &key) {
		node *p = root;
		while(p) {
			if (cmp(key, p->lnode->data->first))
				p = p->left;
			else if (cmp(p->lnode->data->first, key))
				p = p->right;
			else return iterator(this, p->lnode);
		}
		//cout << "baka!" << endl;
		return end();
	}
	const_iterator find(const Key &key) const {
		node *p = root;
		while(p) {
			if (cmp(key, p->lnode->data->first))
				p = p->left;
			else if (cmp(p->lnode->data->first, key))
				p = p->right;
			else return const_iterator(this, p->lnode);
		}
		return cend();
	}
};

}

#endif
