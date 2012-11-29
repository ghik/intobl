'''
Created on Nov 21, 2012

@author: admchr
'''

import sys, os, importlib
from simrunner import plotsim

def compute_mean_stddev(data):
    n = len(data)
    mean = sum(data)/n
    var = sum((d-mean)**2 for d in data)/(n-1)
    stddev = var**0.5
    return mean, stddev

def summarize(datadir, trials):
    import csv
    runs = []
    iters = []
    for i in range(trials):
        fname = datadir+'/result'+str(i)+'.csv'
    
        with open(fname) as f:
            data = csv.reader(f)
            iter = [(int(it), float(d)) for it, d in data]
            runs += [iter]
            if len(iters) == 0:
                iters = [[] for i in iter]
            for i, v in iter:
                iters[i].append(v)
    
    with open(datadir+'/summary.csv', 'w') as f:
        for i, data in enumerate(iters):
            mean, stddev = compute_mean_stddev(data)
            f.write('{},{},{}\n'.format(i,mean,stddev))



class Runner:
    def __init__(self, configuration):
        globaldict = {}
        localdict = {}
        execfile(configuration, globaldict, localdict)
        self.config = localdict['Configuration']
        module = importlib.import_module(self.config.driver)
        self.driver = module.Driver(self.config)
        
    def datadir_root(self):
        return 'datasets/'+self.name
    
    def datadir_path(self, parameters):
        return self.datadir_root()+'/'+self.params_path(parameters)
    
    def params_path(self, parameters):
        name = self.name
        def dirname(param):
            (name, value) = param
            return name + '_' + str(value)
        datadir = '/'.join(['results'] + map(dirname, parameters))
        return datadir
    
    def _prepare_datadir(self, name, parameters, overwrite=False):
        datadir = self.datadir_path(parameters)
        try:
            os.makedirs(datadir) 
        except OSError:
            if not overwrite:
                print 'Dataset with name "{}" for configuration {} already exists'.format(name, parameters)
                sys.exit(1)
        paramsfile = datadir + '/parameters.py'
        with open(paramsfile, 'w') as f:
            f.write(str(dict(parameters)))
            f.close()
        
        return datadir
    
    def combinations(self, paramNames=None):
        if paramNames is None:
            paramNames = self.config.changingParameters
        if len(paramNames) == 0:
            yield []
        else:
            name = paramNames[0]
            values = getattr(self.config, name)
            subc = list(self.combinations(paramNames[1:]))
            for value in values:
                for combination in subc:
                    yield [(name, value)] + combination
    
    def set_dataset(self, name, overwrite=False):
        self.name = name
        self.overwrite = overwrite
    
    def changing_parameters(self):
        l = []
        paramNames = self.config.changingParameters
        for p in paramNames:
            values = getattr(self.config, p)
            l.append((p, values))
        l.sort()
        return l
    
    def _prepare_parameters(self, parameters):
        self._prepare_datadir(self.name, parameters, self.overwrite)
        self.driver.prepare_parameters(parameters)
        
    def run(self):
        changingparameters = self.changing_parameters()
        parameterspace = self.combinations()
        for params in parameterspace:
            datadir = self.datadir_path(params)
            self._prepare_parameters(params)
            self.driver.run(datadir, params)
            summarize(datadir, self.config.repeats)
        plotsim.plot_all(changingparameters, self)
        
