COVERALLS_URL='https://coveralls.io/api/v1/jobs'
CLOVERAGE_VERSION='1.0.7-SNAPSHOT' lein with-profile +test cloverage -o cov --coveralls
lein run -m coveralls
curl -F 'json_file=@cov/coveralls.json' "$COVERALLS_URL"