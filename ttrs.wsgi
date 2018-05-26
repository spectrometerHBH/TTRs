#!/usr/bin/python
import sys
import logging
logging.basicConfig(stream=sys.stderr)
sys.path.insert(0,"/var/www/ttrs/")
from website import app as application
application.secret_key = 'a75#ctyXxeKqoBo7'
