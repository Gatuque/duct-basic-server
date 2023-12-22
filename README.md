# duct-basic-server

This repository is a simple CRUD API of frequently used functions in duct.

- PostgreSQL (docker)
- migration ([duct/migrator.ragtime](https://github.com/duct-framework/migrator.ragtime))
- routes ([reitit](https://github.com/metosin/reitit))
- SQL ([conman](https://github.com/luminus-framework/conman))
- test ([test-containers](https://github.com/testcontainers/testcontainers-clj))

## Developing

### Setup

When you first clone this repository, run:

```sh
lein duct setup
```

This will create files for local configuration, and prep your system
for the project.

### Setup the DB

Run a Postgres DB via Docker:

```
docker run \
--rm -it \
-p 4444:5432 \
--volume postgres-data:/var/lib/postgresql/data \
--name postgres \
-e POSTGRES_USER=dbuser \
-e POSTGRES_DB=testDB \
-e POSTGRES_PASSWORD=dbpassword \
postgres:16.1
```

### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded\q
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 3000}
:initiated
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```
### Check if the DB tables were created successfully
Open a new terminal and run
```sh
docker exec -it postgres psql -U dbuser -d testDB
```
```sql
testDB=# \dt
            List of relations
 Schema |     Name      | Type  |  Owner
--------+---------------+-------+----------
public | duct_server_migrations| table | dbuser
public | users                 | table | dbuser
(2 rows)

testDB=# \q
```
## Legal



Copyright © 2023 FIXME
