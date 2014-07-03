#!/bin/bash

virtualenv env

. env/bin/activate

pip install -e git://github.com/hearsaycorp/richenum
pip install -e git://github.com/hearsaycorp/normalize
pip install lxml requests

echo "type '. env/bin/activate' to activate virtualenv"
