# FFAMS

<FIXME> quick overview

## How to run

```
$ sbt run
```

In case you don't have sbt installed, there's a deployed version at <FIXME>

## How to run tests

Tests are located under `ffams/test/services`, and can be ran using

```
sbt test
```

### Manual testing

Using postman, curl, or any other tool, the system can be tested manually.

* Happy path

```
$ curl --location --request POST 'http://localhost:9000/fake' \
  --header 'Content-Type: text/plain' \
  --header 'Cookie: x-viator-tapersistentcookie=b1733720-834d-42b1-ad39-85304f8d57e6; ssotalogin=redirect' \
  --data-raw 'AAABACCCDA'
```

* Simulate one model is down (see last data char, `Z`)

```
$ curl --location --request POST 'http://localhost:9000/fake' \
  --header 'Content-Type: text/plain' \
  --header 'Cookie: x-viator-tapersistentcookie=b1733720-834d-42b1-ad39-85304f8d57e6; ssotalogin=redirect' \
  --data-raw 'AAABACCCDAZ'
```

* Simulate too much vegetation score is down

```
$ curl --location --request POST 'http://localhost:9000/fake' \
  --header 'Content-Type: text/plain' \
  --header 'Cookie: x-viator-tapersistentcookie=b1733720-834d-42b1-ad39-85304f8d57e6; ssotalogin=redirect' \
  --data-raw 'AAZZ'
```
