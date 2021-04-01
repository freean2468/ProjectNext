import core

# url 2020년 1월1일부터 2020년 12월 31일 까지 설정한 상태
targetUrl = [
    # Riot Blockchain, Inc. (RIOT)
    "https://finance.yahoo.com/quote/RIOT/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # NASDAQ Composite (^IXIC)
    "https://finance.yahoo.com/quote/%5EIXIC/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # Apple Inc. (AAPL) - 3년치 데이터
    # "https://finance.yahoo.com/quote/AAPL/history?period1=1514764800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    
    # Apple Inc. (AAPL) - 1년치 데이터
    "https://finance.yahoo.com/quote/AAPL/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # Nokia Corporation (NOK)
    "https://finance.yahoo.com/quote/NOK/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # GameStop Corp. (GME)
    "https://finance.yahoo.com/quote/GME/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",

    # Tesla, Inc. (TSLA)
    "https://finance.yahoo.com/quote/TSLA/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true"

]

core.crawling(targetUrl)

print('end of crawling')