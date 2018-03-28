#!/bin/sh

openssl genrsa -out Good_RootCA_key.pem

openssl req -key Good_RootCA_key.pem -new -x509 -days 3650 -out Good_RootCA_sertifikat.pem

openssl req -newkey rsa -keyout chip_home_net_key.pem -new -nodes -days 365 -out chip_home_net_sertifikat.crt


openssl x509 -days 365 -CA Good_RootCA_sertifikat.pem -CAkey Good_RootCA_key.pem -req -CAcreateserial -CAserial ca.srl -in chip_home_net_sertifikat.crt -out chip_home_net_potpisani_sertifikat.pem


