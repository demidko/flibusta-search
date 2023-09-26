# Application

This is a template for a complete web application consisting of [frontend](frontend) and [backend](backend) components.

## Build 

Execute `docker build . -t app`. Your image will be located at `docker images -a`. Now you can
run:

```shell
docker run -v `pwd`:`pwd` -w `pwd` -it --rm -p 80:8080 app
```

Web application will available at your [localhost](http://localhost/).



