# Requirements

You'll need docker + docker-compose.

    docker-compose up

Run production.


If you modify a service and need to rebuild the containers you can do so with:

    docker-compose build

Then docker-compose up will reflect the new changes.


# Project layout

Each service has a Dockerfile, this is where I put all the environment variables -- we'll
want a better place to put them such as in the compose config file. Then we can have compose
files for prod/test/local.

# Writing a new service

You can run `docker-compose up` then get the address of the rabbitmq server and use that to
test new services.
