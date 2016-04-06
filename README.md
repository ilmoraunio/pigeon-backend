# pigeon-backend

[![Circle CI](https://circleci.com/gh/rulebased-chat/pigeon-backend.svg?style=svg)](https://circleci.com/gh/rulebased-chat/pigeon-backend) [![Stories in Ready](https://badge.waffle.io/rulebased-chat/pigeon-backend.png?label=ready&title=Ready)](https://waffle.io/rulebased-chat/pigeon-backend) [![Coverage Status](https://coveralls.io/repos/github/rulebased-chat/pigeon-backend/badge.svg?branch=master)](https://coveralls.io/github/rulebased-chat/pigeon-backend?branch=master)

Pigeon is a rulebased messaging service.

## Usage

### Run the application locally

`lein run` or `lein ring server`

### Run the tests

`lein midje`

### Packaging and running as standalone jar

```
lein uberjar
java $JVM_OPTS -jar target/server.jar
```

Make sure to set the following environment variable(s):

```
PORT
```

## License

Copyright © Ilmo Raunio

Released under the MIT license.
