#!/bin/bash

python text-train.py -F 3 -L 3 -f train_file.txt
python text-predict.py -f train_file.txt train_file.txt.model predict_result
python demo.py "$@"
