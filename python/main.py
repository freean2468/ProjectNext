import time

from selenium import webdriver
from bs4 import BeautifulSoup

SCROLL_PAUSE_SEC = 0.2

# selenium에서 사용할 웹 드라이버 상대 경로 정보
driverChrome = './chromedriver'
# selenium의 webdriver에 앞서 설지한 chromediriver를 연동
driver = webdriver.Chrome(driverChrome)

# driver로 특정 페이지를 크롤링
driver.get('https://finance.yahoo.com/quote/RIOT/history?period1=1577836800&period2=1609372800&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true')

last_height = 0
new_height = 0

for i in range(1, 5):
    # 스크롤 높이에 3000만큼 더 가져옴
    new_height += 3000

    # 끝까지 스크롤 다운
    driver.execute_script("window.scrollTo("+str(last_height)+", "+str(new_height)+");")

    time.sleep(SCROLL_PAUSE_SEC)

########################################################################################################

html = driver.page_source
soup = BeautifulSoup(html, 'html.parser')

i = 0
while 1 :
    i = i + 1

    print(i)

    date = soup.select_one(
        '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td.Py\(10px\).Ta\(start\).Pend\(10px\) > span')
    if date is not None:
        print(date.get_text())
    else:
        print(date)
        break

    open = soup.select_one(
        '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(2) > span')
    if open is not None:
        print(open.get_text())
    else:
        print(open)
        break

    high = soup.select_one(
        '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(3) > span')
    if high is not None:
        print(high.get_text())
    else:
        break
        print(high)

    low = soup.select_one(
        '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(4) > span')
    if low is not None:
        print(low.get_text())
    else:
        print(low)
        break

    close = soup.select_one(
        '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(5) > span')
    if close is not None:
        print(close.get_text())
    else:
        print(close)
        break

    volume = soup.select_one(
        '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(7) > span')
    if volume is not None:
        print(volume.get_text())
    else:
        print(volume)
        break

    print("\n")

print('end of loading')
