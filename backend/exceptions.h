#pragma once
class exception {

};

class unallocated_space : public exception {
};

class non_existent_file : public exception {
};

class invalid_format : public exception {
};

class invalid_offset : public exception {
};

class not_found : public exception {
};