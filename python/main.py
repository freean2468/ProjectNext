import core

# 1년치로 끊어서 각 종목의 URL을 입력하세요.
targetUrl = [
    #RIOT 2020
    "https://finance.yahoo.com/quote/RIOT/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # NASDAQ 2020
    "https://finance.yahoo.com/quote/%5EIXIC/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    
    # APPLE 2020
    "https://finance.yahoo.com/quote/AAPL/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # APPLE 2019
    "https://finance.yahoo.com/quote/AAPL/history?period1=1546300800&period2=1577750400&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # APPLE 2018
    "https://finance.yahoo.com/quote/AAPL/history?period1=1514764800&period2=1546214400&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true"
]

core.crawling(targetUrl)

print('end of crawling')