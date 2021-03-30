import core

# url 2020년 1월1일부터 2020년 12월 31일 까지 설정한 상태
targetUrl = [
    #RIOT
    "https://finance.yahoo.com/quote/RIOT/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # NASDAQ
    "https://finance.yahoo.com/quote/%5EIXIC/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true",
    # APPLE
    "https://finance.yahoo.com/quote/AAPL/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true"
]

core.crawling(targetUrl)

print('end of loading')