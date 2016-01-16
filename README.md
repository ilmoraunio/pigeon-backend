# pigeon-backend

Pigeon is a rulebased messaging service.

## Usage

### Run the application locally

`lein ring server`

### Run the tests

`lein midje`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

## License

Copyright © Ilmo Raunio

Released under the MIT license.
