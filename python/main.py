import core
import logging
import threading

format = "%(asctime)s: %(message)s"
logging.basicConfig(format=format, level=logging.INFO, datefmt="%H:%M:%S")

# 1년치로 끊어서 각 종목의 URL을 입력하세요.
targetUrl = [
    # # Riot Blockchain, Inc. (RIOT) 2020
    "https://finance.yahoo.com/quote/RIOT/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # # NASDAQ Composite (^IXIC) 2020
    "https://finance.yahoo.com/quote/%5EIXIC/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # Apple Inc. (AAPL) 2020
    "https://finance.yahoo.com/quote/AAPL/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # Apple Inc. (AAPL) 2019
    "https://finance.yahoo.com/quote/AAPL/history?period1=1546300800&period2=1577750400&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # Apple Inc. (AAPL) 2018
    "https://finance.yahoo.com/quote/AAPL/history?period1=1514764800&period2=1546214400&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # # Nokia Corporation (NOK) 2020
    "https://finance.yahoo.com/quote/NOK/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # # GameStop Corp. (GME) 2020
    "https://finance.yahoo.com/quote/GME/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # # Tesla, Inc. (TSLA) 2020
    "https://finance.yahoo.com/quote/TSLA/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true"
]

i = 0
for url in targetUrl:
    thread = threading.Thread(target=core.crawling, args=(i, logging, url))
    thread.start()
    # thread.join()
    i = i + 1


logging.info("Main    : done")