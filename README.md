# Embedded Sql Reports
A java library that exposes a restful api for the management and consumption of sql reports.

* **embers/admin/** - allows reports to be added, updated or deleted dynamically.
* **embers/query/<query name>** - run the report and return the result via http - as CSV.
* **embers/cached/<query name>** - fetch a cached result, or run and cache if cache miss.

### Requirments:
Embed it in a http container that works with javax.ws.rs, i.e. jetty.

Inject a Datasource to EmbersConfiguration.

The datasource should have access to the 3 tables required by embers, and have read access to the schema the reports are to be run against.

Database tables:

* **queries** - Holds details of the queries that embers/query can run.
* **queries_statistics** - Audit information about calls made to embers/query.
* **query_result_cache** - Caches results for embers/cache service to re-use.

## Build
You can see the build here: https://circleci.com/gh/alfanse/embers

The acceptance tests produce html documentation, my thanks to Dan Bodart for Yatspec that makes this possible (https://github.com/bodar/yatspec):

* admin - https://circle-artifacts.com/gh/alfanse/embers/68/artifacts/0/home/ubuntu/embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/AdminQueriesTest.html
* query - https://circle-artifacts.com/gh/alfanse/embers/69/artifacts/0/home/ubuntu/embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/QueryTest.html
* query statistics - https://circle-artifacts.com/gh/alfanse/embers/68/artifacts/0/home/ubuntu/embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/QueryStatisticsTest.html
* cached - https://circle-artifacts.com/gh/alfanse/embers/68/artifacts/0/home/ubuntu/embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/CachedQueriesTest.html


## Backlog

Add security around admin

Add who ran a query to query_statistics.

