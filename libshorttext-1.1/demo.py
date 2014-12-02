#!/usr/bin/env python

import sys, os
sys.path += ['..']
from libshorttext.analyzer import *

if __name__ == '__main__':
    predict_result = InstanceSet('predict_result')
    analyzer = Analyzer('train_file.txt.model')
    insts = predict_result.select(with_labels(['concept', 'location', 'definition', 'description', 'multiplechoice', 'amount', 'organization', 'other', 'person', 'abbreviation', 'verb']), sort_by_dec, subset(1000))
    analyzer.info(insts)
    analyzer.gen_confusion_table(insts)
    insts.load_text()
    tmp = ""
    for s in sys.argv [1:]:
        tmp += s + " "
    tmp = tmp[:-1]
#    print(insts)
#    analyzer.analyze_single(insts[0], 3)
    print(tmp)
    analyzer.analyze_single(tmp, 5)
