#!/bin/bash

virtualenv env

. env/bin/activate

pip install -r requirements.txt -r requirements-dev.txt

echo "type '. env/bin/activate' to activate virtualenv"
