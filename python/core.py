import time

from selenium import webdriver
from bs4 import BeautifulSoup
import requests
import urllib
import datetime
import platform

localhost = "http://127.0.0.1:8080/"

SCROLL_PAUSE_SEC = 0.2

def crawling (targetUrlList):
    # selenium에서 사용할 웹 드라이버 상대 경로 정보
    driverChrome = ''

    if platform.system() == 'Windows':
        driverChrome = './chromedriver_win'
    else:
        driverChrome = './chromedriver'

    # selenium의 webdriver에 앞서 설지한 chromediriver를 연동
    driver = webdriver.Chrome(driverChrome)

    for targetUrl in targetUrlList:
        # driver로 특정 페이지를 크롤링
        driver.get(targetUrl)

        last_height = 0
        new_height = 0

        #1년치는 for문 5번만 돌려도 됨
        for i in range(1, 11):
            # 스크롤 높이에 3000만큼 더 가져옴
            new_height += 3000

            # 끝까지 스크롤 다운
            driver.execute_script("window.scrollTo("+str(last_height)+", "+str(new_height)+");")

            time.sleep(SCROLL_PAUSE_SEC)

        html = driver.page_source
        soup = BeautifulSoup(html, 'html.parser')

        ticker = soup.select_one('#quote-header-info > div.Mt\(15px\) > div.D\(ib\).Mt\(-5px\).Mend\(20px\).Maw\(56\%\)--tab768.Maw\(52\%\).Ov\(h\).smartphone_Maw\(85\%\).smartphone_Mend\(0px\) > div.D\(ib\) > h1')
        ticker = ticker.get_text()
        ticker = ticker.split(')')[-2].split('(')[-1]

        beforeDate = soup.select_one(
                '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child(2) > td.Py\(10px\).Ta\(start\).Pend\(10px\) > span').get_text()

        jsonArray = []

        if crawlingLoopImpl(jsonArray) is True:
            route = "dailies"
            requestUrl = localhost + route
            r = requests.post(requestUrl, json=jsonArray)
            # print(r.text)



def crawlingLoopImpl(jsonArray):
    i = 0
    while 1 :
        i = i + 1

        if i >= 2:
            break

        print(i)

        date = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td.Py\(10px\).Ta\(start\).Pend\(10px\) > span')

        mysqlDateForm = datetime.datetime.strptime(date.get_text(), '%b %d, %Y').strftime("%Y-%m-%d")

        # 크롤링하기 전 이미 크롤링되어 있는지 확인
        route = "isTickerDates/1?"
        params = {'ticker': ticker, 'year': mysqlDateForm[0:4]}
        requestUrl = localhost + route + urllib.parse.urlencode(params)
        r = requests.get(requestUrl)
        if int(r.text) < 250:
            ""

        if date is None:
            print("date is None!!! break")
            break

        print("comparing %s with %s : %s" % (beforeDate, date.get_text(), beforeDate == date.get_text()))

        if date.get_text() == beforeDate:
            continue

        open = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(2) > span')

        if str(open)[:-6].find('span') <= 0:
            continue
        else:
            print(open + " : open break")
            break

        high = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(3) > span')
        if high is not None:
            ""
            # print(high.get_text())
        else:
            print(high + " : high break")
            break

        low = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(4) > span')
        if low is not None:
            ""
            # print(low.get_text())
        else:
            print(low + " : low break")
            break

        close = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(5) > span')
        if close is not None:
            ""
            # print(close.get_text())
        else:
            print(close + " : close break")
            break

        volume = soup.select_one(
            '#Col1-1-HistoricalDataTable-Proxy > section > div.Pb\(10px\).Ovx\(a\).W\(100\%\) > table > tbody > tr:nth-child('+ str(i) +') > td:nth-child(7) > span')
        if volume is not None:
            ""
            # print(volume.get_text())
        else:
            print(volume + " : volume break")
            break

        beforeDate = date.get_text()

        jsonArray.append({'ticker': ticker, 'date': mysqlDateForm, 'open': open.get_text().replace(",", ""),
                  'high': high.get_text().replace(",", ""), 'low': low.get_text().replace(",", ""), 'close': close.get_text().replace(",", ""),
                  'volume': volume.get_text().replace(",", "")
        })