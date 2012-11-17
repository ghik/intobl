#!/usr/bin/env python
'''
Created on 07-11-2012

@author: admchr
'''

from simrunner.Configuration import Configuration
from simrunner import plotsim
import os
import subprocess
import sys
import shutil

devnull = open('/dev/null', 'w')

def dirname(param):
    (name, value) = param
    return name + '_' + str(value)

def prepare_datadir(name, parameters):
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
    
    return datadir

paramsFileHeader = """
from implementation.BaseParameters import BaseParameters

class Parameters(BaseParameters):
"""

def prepare_parameters_file(script, params):
    paramsFileContent = paramsFileHeader
    
    for param in Configuration.constantParameters:
        paramsFileContent += "    {} = {}\n".format(param, repr(getattr(Configuration, param)))
    
    for param in params:
        (name, value) = param
        paramsFileContent += "    {} = {}\n".format(name, repr(value))
        
    paramsFileName = os.path.join(os.path.dirname(script), 'implementation/Parameters.py')
    paramsFile = open(paramsFileName, 'w')
    paramsFile.write(paramsFileContent)
    paramsFile.close()

def summarize(datadir, trials):
        
    runs = []
    for i in range(trials):
        fname = datadir+'/result'+str(i)+'.csv'
    
        with open(fname) as f:
            data = csv.reader(f)
            runs += [[(float(it), float(d)) for it, d in data]]
    with open(datadir+'/summary.csv', 'w') as f:
        for i, data in enumerate(iteration_data):
            n = len(data)
            mean = sum(data)/n
            stddev = sum((d-mean)**2 for d in data)/(n-1)
            f.write('{},{},{}\n'.format(i,mean,stddev))

def runsim(script, datadir, trials):
    for i in range(trials):
        subprocess.check_call([script], stdout=devnull)
        shutil.move('./stats.txt', datadir+'/result{}.csv'.format(i))

def combinations(paramNames):
    if len(paramNames) == 0:
        yield []
    else:
        name = paramNames[0]
        values = getattr(Configuration, name)
        subc = list(combinations(paramNames[1:]))
        for value in values:
            for combination in subc:
                yield [(name, value)] + combination


def run(argv):
    script = argv[1]
    name = argv[2]
    trials = int(argv[3])
    
    if not os.path.exists(script):
        print 'Could not find script {}'.format(script)
        sys.exit(1)
    
                    
    for params in combinations(Configuration.changingParameters):
        datadir = prepare_datadir(name, params)
        prepare_parameters_file(script, params)
        runsim(script, datadir, trials)
        plotsim.plot_results(datadir)
    

