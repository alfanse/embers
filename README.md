# Embedded Sql Reports
[![Build Status](https://circleci.com/gh/alfanse/embers.svg?branch=master)](https://circleci.com/gh/alfanse/embers)
[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)](https://www.java.com/)

A java library that exposes a restful api for the management and consumption of sql reports.

* **embers/admin/** - allows reports to be added, updated or deleted dynamically.
* **embers/query/<query name>** - run the report and return the result via http - as CSV.
* **embers/cached/<query name>** - fetch a cached result, or run and cache if cache miss.

### Requirements:
Embed it in a http container that works with javax.ws.rs, i.e. jetty.

Inject a Datasource to EmbersConfiguration.

The datasource should have access to the 3 tables required by embers, and have read access to the schema the reports are to be run against.

Database tables:

* **queries** - Holds details of the queries that embers/query can run.
* **queries_statistics** - Audit information about calls made to embers/query.
* **query_result_cache** - Caches results for embers/cache service to re-use.

## Build
Thanks to circleci, you can see the build here: https://circleci.com/gh/alfanse/embers

The acceptance tests produce html documentation, my thanks to Dan Bodart for Yatspec that makes this possible (https://github.com/bodar/yatspec):
To find the documentation, on circle-ci, latest green build / artifacts tab and drill down to:
* admin - /embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/AdminQueriesTest.html
* query - /embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/QueryTest.html
* query statistics - /embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/QueryStatisticsTest.html
* cached - /embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/acceptance/CachedQueriesTest.html
* e2e - a worked examples, maintaining a query - https://92-28635373-gh.circle-artifacts.com/0/home/circleci/alfanse/embers/embers-acceptance-tests/build/reports/acceptance/adf/embers/e2e/PuttingItAllTogetherTest.html

## Code Coverage
powered by jacoco plugin
run:
 gradlew clean codeCoverageReport
see reports here:
 embers/build/reports/jacoco/index.html

## Backlog

Add security around admin

Add who ran a query to query_statistics.

