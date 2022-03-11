import logging

logger = logging.getLogger("ai-regression-server")
logger.level = logging.INFO

formatter = logging.Formatter(
    '%(name)s | %(asctime)s %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S')

c_handler = logging.StreamHandler()
f_handler = logging.FileHandler('localLog.txt')

c_handler.setLevel(logger.level)
f_handler.setLevel(logger.level)
c_handler.setFormatter(formatter)
f_handler.setFormatter(formatter)

logger.addHandler(c_handler)
logger.addHandler(f_handler)
