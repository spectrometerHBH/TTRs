#include <array>
#include <iostream>
#include <strstream>
#include <boost/asio.hpp>


// Synchronous echo client.

using boost::asio::ip::tcp;

enum {
  BUF_SIZE = 1024
};

int main(int argc, char* argv[]) {
  if (argc != 3) {
    std::cerr << "Usage: " << argv[0] << " <host> <port>" << std::endl;
    return 1;
  }

  const char* host = argv[1];
  const char* port = argv[2];

  boost::asio::io_context ioc;

  // NOTE:
  // We don't use output parameter error_code in this example.
  // Using exception handling could largely simplify the source code.
  try {
    tcp::resolver resolver(ioc);

    // Return type: tcp::resolver::results_type
    auto endpoints = resolver.resolve(tcp::v4(), host, port);

    // Don't use socket.connect() directly.
    // Function connect() calls socket.connect() internally.


    // Get user input.
    while (true){

        char request[BUF_SIZE];
        std::size_t request_length = 0;
        do {
            std::cout << ">> ";
            std::cin.getline(request, BUF_SIZE);
            request_length = strlen(request);
        } while (request_length == 0);
        tcp::socket socket(ioc);
        boost::asio::connect(socket, endpoints);
        boost::asio::write(socket, boost::asio::buffer(request, request_length+1));
        
        boost::asio::streambuf buff;
        size_t n = boost::asio::read_until(socket, buff, '\0');


        std::string response;
        std::istream is(&buff);
        std::getline(is, response);

        std::cout << "  " << response << std::endl;
    }
    // Read the response.
    // Use global read() or not (please note the difference).

    /*std::cout << "Reply is: ";

    std::size_t total_reply_length = 0;
    while (true) {
      std::array<char, BUF_SIZE> reply;
      std::size_t reply_length = socket.read_some(boost::asio::buffer(reply));

      std::cout.write(reply.data(), reply_length);

      total_reply_length += reply_length;
      if (total_reply_length >= request_length) {
        break;
      }
    }


    std::cout << std::endl;*/

  } catch (std::exception& e) {
    std::cerr << e.what() << std::endl;
  }

  return 0;
}