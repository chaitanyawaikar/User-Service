# User Service Application

This is a template service that performs four activities
1. Fetch users by id
2. Fetch all users
3. Setup all the data
4. Create a new user

## How to run the application

To run this application, start the sbt shell and type
##### run 

This will start the user service at port 9000 by default. Please hit the /setup endpoint that will create the table in H2 in memory database and insert the data into the database. This is purposely created as it is OS agnostic and works across all platforms.

## Tech stack used
Scala, Play framework, H2 database(in memory), Slick, Scalafmt for code hygiene, Scalatest, Mockito

H2 has been specifically chosen to avoid any external DB installations.

#### Endpoints

There are four endpoints

/setup :- For setting up all the data

/users :- Fetch all the users

/users/:userId  :- Fetch user by id

POST /user :- To create a new user

#### Tech Debt

Unit test cases missing for user repository

End to end test remaining for the application

Input parameter validation missing for create user endpoint. Currently there is no validation in place to check the user input parameters. Best solution is to use Cats for IO Validation
