// echo_server_sync.cpp
#include <boost/asio.hpp>
#include <boost/array.hpp>
#include <iostream>
#include <strstream>
#include "command.h"
#include <cstring>

#include <ctime>
using boost::asio::ip::tcp;
int Session(tcp::socket socket) {
  try {
    char bin[1024];
    char bout[1024];
    memset(bout,0,1024);
    String<20> username;
    String<20> password;
    int a = 0;
    //while (true) {
      //boost::asio::streambuf bin;
      //boost::asio::streambuf bout;
      boost::system::error_code ec;
      //size_t n = boost::asio::read_until(socket, bin, '\0');
      
      //ostringstream os(bout);
      socket.read_some(boost::asio::buffer(bin,1024));
      std::istringstream is(bin);
      std::ostringstream os(bout);

      char response[1024] = "";
      

      read_command(is, os);
    
      //sleep(60);
      //char response[1024];
      strcpy(response, os.str().c_str());
      //std::cout << strlen(os.str().c_str())<< ' ' <<strlen(response) << std::endl;
      socket.write_some(boost::asio::buffer(response,strlen(response)));
    //}
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
