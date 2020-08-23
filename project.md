# FFAMS

The solution for this challenge is built using Scala and the Play Framework (The Rails of Scala 🙂).

As proposed in the general architecture documentation, the core logic exists in a 'FFAMS' component. 
This component is responsible for receiving the image requests and orchestrating the calls to the classifier and scoring models.
The FFAMS component delegates the image analysis to a specific strategy to make it easy to replace the strategy in the future (updating or A/B testing)

We do a couple of simplifications in the code:

* We have fake clients to interact with both the classifier and the scoring gateway. The clients have some local logic instead of calling another component
* We don't send an alert to an event-bus for the same reason: it would be cumbersome to add that infrastructure in this example project
* We represent an Image as a list of chars, each representing a type of vegetation. For instance "AABC" would represent a 2x2 image where we could see 3 types of vegetations
* Dependency Injection is not properly made. TBH this was due to lack of time: the Play Framework has been changing a lot some internal details and I decided to skip that part as a trade-off.

## How to run

```
$ sbt run
```

In case you don't have sbt installed, there's a deployed version at https://ffams.herokuapp.com/

## How to run tests

Tests are located under `ffams/test/services`, and can be ran using

```
sbt test
```

### Manual testing

Using postman, curl, or any other tool, the system can be tested manually.

* Happy path

```
$ curl --location --request POST 'https://ffams.herokuapp.com/fake' \
  --header 'Content-Type: text/plain' \
  --header 'Cookie: x-viator-tapersistentcookie=b1733720-834d-42b1-ad39-85304f8d57e6; ssotalogin=redirect' \
  --data-raw 'AAABACCCDA'
```

* Simulate one model is down (see last data char, `Z`)

```
$ curl --location --request POST 'https://ffams.herokuapp.com/fake' \
  --header 'Content-Type: text/plain' \
  --header 'Cookie: x-viator-tapersistentcookie=b1733720-834d-42b1-ad39-85304f8d57e6; ssotalogin=redirect' \
  --data-raw 'AAABACCCDAZ'
```

* Simulate too much vegetation score is down

```
$ curl --location --request POST 'https://ffams.herokuapp.com/fake' \
  --header 'Content-Type: text/plain' \
  --header 'Cookie: x-viator-tapersistentcookie=b1733720-834d-42b1-ad39-85304f8d57e6; ssotalogin=redirect' \
  --data-raw 'AAZZ'
```
