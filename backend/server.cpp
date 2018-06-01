// echo_server_sync.cpp
#include <boost/asio.hpp>
#include <boost/array.hpp>
#include <iostream>
#include <strstream>
#include "command.h"
#include <cstring>
#define bufsize 65536
#include <ctime>
using boost::asio::ip::tcp;
int Session(tcp::socket socket) {
  try {
    char bin[bufsize];
    char bout[bufsize];
    memset(bout,0,bufsize);
    String<20> username;
    String<20> password;
    int a = 0;
    //while (true) {
      //boost::asio::streambuf bin;
      //boost::asio::streambuf bout;
      boost::system::error_code ec;
      //size_t n = boost::asio::read_until(socket, bin, '\0');
      
      //ostringstream os(bout);
      socket.read_some(boost::asio::buffer(bin,bufsize));
      std::istringstream is(bin);
      std::ostringstream os(bout);

      char * response = "";
      

      read_command(is, os);
      os << "\0";      

      //sleep(60);

      //char response[1024];
      response = new char[os.str().size() + 1];
      strcpy(response, os.str().c_str());
      socket.write_some(boost::asio::buffer(response,os.str().length()));

  } catch (std::exception& e) {
    std::cerr << "Exception: " <<  e.what() << std::endl;
  }
  return 0;
}

int main(int argc, char* argv[]) {
  if (argc != 2) {
    std::cerr << "Usage: " << argv[0] << " <port>" << std::endl;
    return 1;
  }

  unsigned short port = std::atoi(argv[1]);

  boost::asio::io_context ioc;
  tcp::acceptor acceptor(ioc, tcp::endpoint(tcp::v4(), port));

  try {
    while (true) {
      if ( Session(acceptor.accept()) == -1) break;
    }
  } catch (std::exception& e) {
    std::cerr << e.what() << std::endl;
  }

  return 0;
}
