# Updating ring-swagger-ui (or libraries dependent upon it)

When updating `ring-swagger-ui`

- Update `index.html`
- After updating `index.html`, add the current implementation of `addApiKeyAuthorization` from the previous `index.html` (or: https://github.com/swagger-api/swagger-ui/issues/818#issuecomment-113185459) to enable passing the JWT in the Authorization header