## Changelogs

### v1.0.0

- initial AuthClient build
- added sync HTTP client to interact with authservice
- added in-memory caching support
- added redis caching support

### v1.0.1

- logging updated to use slf4j
- added withDebug() method to RedisConnectionProperty
- added ping() method to check redis connectivity in RedisAuthTokenCache
- minor bug fixes

### v1.1.0

- added support for spring boot autoconfiguration