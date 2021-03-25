from bs4 import BeautifulSoup
import requests

# url 2020년 1월1일부터 2020년 12월 31일 까지 설정한 상태 (총 252일)
url = "https://finance.yahoo.com/quote/RIOT/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true"

response = requests.get(url)

if response.status_code == 200:

    html = response.text
    soup = BeautifulSoup(html, 'html.parser')

    # 4개월씩 3개
    i = 0
    for i in range(1, 84):   #12월~9월(4개월)
        print(i)

        date = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td.Py\(10px\).Ta\(start\).Pend\(10px\) > span')
        if date is not None:
            print(date.get_text)
        else:
            print(date)

        open = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(2) > span')
        if open is not None:
            print(open.get_text())
        else:
            print(open)

        high = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(3) > span')
        if high is not None:
            print(high.get_text())
        else:
            print(high)

        low = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(4) > span')
        if low is not None:
            print(low.get_text())
        else:
            print(low)

        close = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(5) > span')
        if close is not None:
            print(close.get_text())
        else:
            print(close)

        volume = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(7) > span')
        if volume is not None:
            print(volume.get_text())
        else:
            print(volume)

        print("\n")

else:
    print(response.status_code)

    wwww.mm.dd
    String: date
    double: 4
    int: 1
