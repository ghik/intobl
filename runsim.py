#!/usr/bin/env python

import sys, os, subprocess, csv

devnull = open('/dev/null','w')

outname=sys.argv[1]
trials=30
datadir = 'data/'+outname
try:
    os.mkdir(datadir)
except OSError:
    print 'Data folder with name "{}" already exists'.format(outname)
    #os.exit(1)
    
runs = []
for i in range(trials):
    fname = datadir+'/result'+str(i)+'.csv'
    subprocess.check_call(['snippets/genetic.py', fname], stdout=devnull)

    with open(fname) as f:
        data = csv.reader(f)
        runs += [[(float(it), float(d)) for it, d in data]]

iterations = len(runs[0])
iteration_data = [[] for i in range(iterations)]
for i in range(iterations):
    for run in runs:
        iteration_data[i].append(run[i][1])

with open(datadir+'/summary.csv', 'w') as f:
    for i, data in enumerate(iteration_data):
        n = len(data)
        mean = sum(data)/n
        stddev = sum((d-mean)**2 for d in data)/(n-1)
        f.write('{},{},{}\n'.format(i,mean,stddev))
    
