#!/bin/bash

# --------------------------------------------------
# Monster Trading Cards Game Testing Script
# --------------------------------------------------

echo "CURL Testing for Monster Trading Cards Game"
echo "--------------------------------------------"
echo "Syntax: ./mtcg_test.sh"
echo

PAUSE_ENABLED=0
for arg in "$@"; do
  if [[ "$arg" == "pause" ]]; then
    PAUSE_ENABLED=1
  fi
done

function pause() {
  if [[ $PAUSE_ENABLED -eq 1 ]]; then
    read -p "Press [Enter] to continue..."
  fi
}

# --------------------------------------------------
echo "1) Create Users (Registration)"
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d '{"Username":"kienboec", "Password":"daniel"}'
echo "Should return HTTP 201"
echo
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d '{"Username":"altenhof", "Password":"markus"}'
echo "Should return HTTP 201"
echo
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d '{"Username":"admin", "Password":"istrator"}'
echo "Should return HTTP 201"
echo

pause

echo "Should fail:"
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d '{"Username":"kienboec", "Password":"daniel"}'
echo "Should return HTTP 4xx - User already exists"
echo
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d '{"Username":"kienboec", "Password":"different"}'
echo "Should return HTTP 4xx - User already exists"
echo

pause

# --------------------------------------------------
echo "2) Login Users"
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d '{"Username":"kienboec", "Password":"daniel"}'
echo "Should return HTTP 200 with generated token for the user, e.g., kienboec-mtcgToken"
echo
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d '{"Username":"altenhof", "Password":"markus"}'
echo "Should return HTTP 200 with generated token for the user, e.g., altenhof-mtcgToken"
echo
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d '{"Username":"admin", "Password":"istrator"}'
echo "Should return HTTP 200 with generated token for the user, e.g., admin-mtcgToken"
echo

pause

echo "Should fail:"
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d '{"Username":"kienboec", "Password":"different"}'
echo "Should return HTTP 4xx - Login failed"
echo

pause

# --------------------------------------------------
echo "3) Create Packages (done by admin)"
curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" -d '[{"Id":"845f0dc7-37d0-426e-994e-43fc3ac83c08", "Name":"WaterGoblin", "Damage":10.0}, {"Id":"99f8f8dc-e25e-4a95-aa2c-782823f36e2a", "Name":"Dragon", "Damage":50.0}, {"Id":"e85e3976-7c86-4d06-9a80-641c2019a79f", "Name":"WaterSpell", "Damage":20.0}, {"Id":"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334", "Name":"Ork", "Damage":45.0}, {"Id":"dfdd758f-649c-40f9-ba3a-8657f4b3439f", "Name":"FireSpell", "Damage":25.0}]'
echo "Should return HTTP 201"
echo

pause

# --------------------------------------------------
echo "17) Battle (Start battle between users)"
curl -i -X POST http://localhost:10001/battles --header "Authorization: Bearer kienboec-mtcgToken" &
curl -i -X POST http://localhost:10001/battles --header "Authorization: Bearer altenhof-mtcgToken" &
echo "Battles started between kienboec and altenhof"
echo

pause

# --------------------------------------------------
echo "18) Stats"
echo "Stats for kienboec:"
curl -i -X GET http://localhost:10001/stats --header "Authorization: Bearer kienboec-mtcgToken"
echo
echo "Stats for altenhof:"
curl -i -X GET http://localhost:10001/stats --header "Authorization: Bearer altenhof-mtcgToken"
echo

pause

# --------------------------------------------------
echo "End of script."
