#!/usr/bin/env python
'''
Created on 07-11-2012

@author: admchr
'''

import csv
import os
import subprocess
import sys

script = sys.argv[1]
config = eval(open(sys.argv[2]).read())
name = sys.argv[3]
trials = int(sys.argv[4])

devnull = open('/dev/null', 'w')

def dirname(param):
    (name, value) = param
    return name + '_' + str(value)

def runsim(script, name, trials, parameters):
    if not os.path.exists(script):
        print 'Could not find script {}'.format(script)
        sys.exit(1)
    
    datadir = '/'.join(['data', name] + map(dirname, parameters))
    try:
        os.makedirs(datadir) 
    except OSError:
        print 'Dataset with name "{}" for configuration {} already exists'.format(name, parameters)
        sys.exit(1)
        
    paramsfile = datadir + '/parameters.py'
    with open(paramsfile, 'w') as f:
        f.write(str(dict(parameters)))
        f.close()
    
    runs = []
    for i in range(trials):
        datafile = datadir + '/result' + str(i) + '.csv'
        subprocess.check_call(['./' + script, paramsfile, datafile], stdout=devnull)
    
        with open(datafile) as f:
            data = csv.reader(f)
            runs += [[(float(it), float(d)) for it, d in data]]
            f.close()
    
    iterations = len(runs[0])
    iteration_data = [[] for i in range(iterations)]
    for i in range(iterations):
        for run in runs:
            iteration_data[i].append(run[i][1])
    
    with open(datadir + '/summary.csv', 'w') as f:
        for i, data in enumerate(iteration_data):
            n = len(data)
            mean = sum(data) / n
            stddev = sum((d - mean) ** 2 for d in data) / (n - 1)
            f.write('{},{},{}\n'.format(i, mean, stddev))
        f.close()

def combinations(config):
    if len(config) == 0:
        yield []
    else:
        (name, values) = config[0]
        for value in values:
            for combination in combinations(config[1:]):
                yield [(name, value)] + combination

for params in combinations(config):
    runsim(script, name, trials, params)
