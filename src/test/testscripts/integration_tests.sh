#!/bin/bash

# --------------------------------------------------
echo "1) Create Users (Registration)"

# Create User
echo "Creating user 'kienboec'"
curl -i -X POST http://localhost:10001/users \
     --header "Content-Type: application/json" \
     -d '{"username":"kienboec", "password":"daniel"}'
echo "Should return HTTP 201"
echo

echo "Creating user 'altenhof'"
curl -i -X POST http://localhost:10001/users \
     --header "Content-Type: application/json" \
     -d '{"username":"altenhof", "password":"markus"}'
echo "Should return HTTP 201"
echo

echo "Creating user 'admin'"
curl -i -X POST http://localhost:10001/users \
     --header "Content-Type: application/json" \
     -d '{"username":"admin", "password":"istrator"}'
echo "Should return HTTP 201"
echo

# Test user creation failure
echo "Should fail:"
curl -i -X POST http://localhost:10001/users \
     --header "Content-Type: application/json" \
     -d '{"username":"kienboec", "password":"daniel"}'
echo "Should return HTTP 4xx - User already exists"
echo

curl -i -X POST http://localhost:10001/users \
     --header "Content-Type: application/json" \
     -d '{"username":"kienboec", "password":"different"}'
echo "Should return HTTP 4xx - User already exists"
echo
echo

# --------------------------------------------------
echo "2) Login Users"

# Login Users
echo "Logging in user 'kienboec'"
curl -i -X POST http://localhost:10001/sessions \
     --header "Content-Type: application/json" \
     -d '{"username":"kienboec", "password":"daniel"}'
echo "Should return HTTP 200 with generated token for the user, e.g., 
kienboec-mtcgToken"
echo

echo "Logging in user 'altenhof'"
curl -i -X POST http://localhost:10001/sessions \
     --header "Content-Type: application/json" \
     -d '{"username":"altenhof", "password":"markus"}'
echo "Should return HTTP 200 with generated token for the user, e.g., 
altenhof-mtcgToken"
echo

echo "Logging in user 'admin'"
curl -i -X POST http://localhost:10001/sessions \
     --header "Content-Type: application/json" \
     -d '{"username":"admin", "password":"istrator"}'
echo "Should return HTTP 200 with generated token for the user, e.g., 
admin-mtcgToken"
echo

# Test login failure
echo "Should fail:"
curl -i -X POST http://localhost:10001/sessions \
     --header "Content-Type: application/json" \
     -d '{"username":"kienboec", "password":"different"}'
echo "Should return HTTP 4xx - Login failed"
echo

