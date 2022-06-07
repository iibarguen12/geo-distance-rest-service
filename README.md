# geo-distance-rest-service
## Rest service to calculate distance between geographic locations

This project uses Spring security and JWT for authentication and authorization, so to login you can use either the admin or user profile with the follow credentials:
user: wcc_user
pass: user

user: wcc_admin
pass: admin

The project is divided in two main parts, a Spring Boot app as REST API and a MySQL database to persist the information. It also counts whit a docker-compose to deploy everything by itself so asl log you have Docker in your machine you don't need to worry about configuring any of those, just situate in the folder project and run the command docker-compose up, and it will create a container whit 3 docker images. one image for the geographic-distance-app in the port 8081, other for the mysql:5.7 in the port 3406, and the last for the adminer in the port 9000, this last will helps you in case you don't have a MySQL Workbench instance in your computer.

Once started, the project creates the respectibe tables in MySQL and inserts some users and roles to be able to use the app, and then, it fetchs the first 100 records of the http://www.freemaptools.com/download/full-postcodes/postcodes.zip file with the information of postacl codes from UK.

The different endpoints to acces are:
1. UserResource:
  - Login: http://localhost:8081/v1/geo-distance/login
  - Get Users: http://localhost:8081/v1/geo-distance/management/users
  - Add User: http://localhost:8081/v1/geo-distance/management/user/save
  - Add Role to User: http://localhost:8081/v1/geo-distance/management/role/add-to-user 
2. PostcodeResource
  - Get Poscodes: http://localhost:8081/v1/geo-distance/postcodes
  - Save or Update Postcode: http://localhost:8081/v1/geo-distance/postcode/save
  - Save or Update multiple Poscodes: http://localhost:8081/v1/geo-distance/postcodes/save
  - Get the distance between two Postcodes: http://localhost:8081/v1/geo-distance/postcodes/distance

There is also a Postman collection with all the test for each endpoint, and as well there is a JUnit test for each one inside the project.

Thanks for reading and regards
