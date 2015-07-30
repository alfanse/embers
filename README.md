# Embedded Sql Reports
A java library that exposes a restful api for the management and consumption of sql reports.

* ‘’’embers/admin/‘’’ - allows reports to be added, updated or deleted dynamically.
* ‘’’embers/query/<query name>’’’ - run the report and return the result via http - as CSV.
* ‘’’embers/cached/<query name>’’’ - fetched a cached result, or run and cache if cache miss.

Requires:
Embed it in a http container that works with javax.ws.rs, i.e. jetty.

Injection of a Datasource to EmbersConfiguration. 

This datasource should have access to the 3 tables required by embers, and have read only access to the schema the reports are to be run against.

Database tables:

* ‘’’queries’’’ - Holds details of the queries that the Embers QueryHandler can run.
* ‘’’queries_statistics’’’ - Audit information about calls made to embers/query.
* ‘’’query_result_cache’’’ - Caches results for embers/cache service to re-use.


### Backlog

Add security around admin

Add who ran  a query to query_statistics.
