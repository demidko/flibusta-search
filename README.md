# flibusta-search

Поиск на Флибусте по автору и фразе.

## Предпосылки

Иногда мне бывает интересно, как развивалась некая тема в произведениях определенного автора, и появляется желание выцепить все его книги, этот аспект затрагивающие. Так родилась идея искать с указанием автора и нужных фраз. Подобым инструментом является сервис Google Книги, однако, зачастую он не знает про многие произведения, которые есть на Флибусте, или знает, но не находит, или находит нерелевантные вещи. 

## Почему Флибуста?

Легче всего скачать и искать по тексту. В будущем возможна поддержка других бесплатных библиотек. Также смотрите [интересную дискуссию](https://habr.com/ru/articles/586814/) по теме пиратства.

## Запуск через Docker

Соберите приложение командой `docker build . -t flibusta-search`. Теперь запускайте:

```shell
docker run -v `pwd`:`pwd` -w `pwd` -it --rm -p 80:8080 flibusta-search
```

Приложение станет доступно на вашем [localhost'е](http://localhost/) через браузер. Если вы еще не знакомы с Docker, то быстро удалить приложение и весь кеш можно будет командой `docker system prune -fa`.
