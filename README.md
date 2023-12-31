# Flibusta Search

Поиск на Флибусте по автору и фразе.

## Как пользоваться?

Веб-приложение доступно по адресу [ссылка будет позже]() c любого устройства.

## Предпосылки

Иногда мне бывает интересно, как развивалась некая тема в произведениях определенного автора, и появляется желание
выцепить все его книги, этот аспект затрагивающие. Так родилась идея искать с указанием автора и нужных фраз. Подобным
инструментом является сервис Google Книги, однако, зачастую он не знает про многие произведения, которые есть на
Флибусте, или знает, но не дает посмотреть страницу с результатом из-за ограничений авторского права. Сейчас
поддерживается поиск по морфологической основе запроса (леммы и стеммы), а в будущем планируется добавить предобученную
модель с поддержкой тематического поиска.

## Почему Флибуста?

Флибуста одна из самых больших библиотек на русском языке, легко скачивать fb2 и искать по тексту. Также
смотрите [интересную дискуссию](https://habr.com/ru/articles/586814) по теме пиратства.

## Запуск через Docker

Этот вариант позволяет запустить приложение локально на вашем компьютере. Системные требования: от 2ГБ оперативной
памяти и выше.

```shell
docker run -v `pwd`:`pwd` -w `pwd` -it --rm -p 80:8080 ghcr.io/demidko/flibusta-search:main
```

Или так, если предыдущий вариант не работает

```shell
docker run --platform linux/amd64 -v `pwd`:`pwd` -w `pwd` -it --rm -p 80:8080 ghcr.io/demidko/flibusta-search:main
```

Приложение станет доступно на вашем [localhost'е](http://localhost) через браузер. Кеш приложения будет находиться в
директории `./cache`. Если вы еще не знакомы с Docker, то быстро удалить приложение можно будет
командой `docker system prune -fa`. Папку с кешем нужно будет удалить вручную.