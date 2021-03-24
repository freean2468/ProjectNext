from bs4 import BeautifulSoup
from urllib.request import urlopen

print('hh')
html = urlopen("https://finance.naver.com/item/main.nhn?code=005930")
print('hh')

bsObject = BeautifulSoup(html, "html.parser")
print('hh')

no_today = bsObject.find("p", {"class":"no_today"})
print('hh')
print(no_today)

print('hh')