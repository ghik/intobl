#!/usr/bin/env python
'''
Created on 07-11-2012

@author: ghik
'''
import pyevolve.DBAdapters
from pyevolve.DBAdapters import DBBaseAdapter
import numpy
import pyevolve.G1DBinaryString
import pyevolve.GSimpleGA
import pyevolve.Selectors
import sys

def rastrigin(x):
    n = len(x)
    A = 10
    return A * n + sum(x ** 2 - A * numpy.cos(2 * numpy.pi * x))

bitsegment = 2**32

def parse_to_point(binary_string, minx= -5.0, miny= -5.0, maxx=5.0, maxy=5.0):
    dec = binary_string.getDecimal()
    x = minx + ((dec / bitsegment) / float(bitsegment)) * (maxx - minx)
    y = miny + ((dec % bitsegment) / float(bitsegment)) * (maxy - miny)
    return (x, y)

def eval_func(individual):
    return 100.0 - rastrigin(numpy.array(parse_to_point(individual)))
   
class FileAdapter(DBBaseAdapter):
    def __init__(self, outfile_max, outfile_avg, **kwargs):
        DBBaseAdapter.__init__(self, **kwargs)
        self.i = 0
        self.outfile_max = outfile_max
        self.outfile_avg = outfile_avg
        
    def open(self, ga):
        self.fmax = open(outfile_max, 'wt')
        self.favg = open(outfile_avg, 'wt')
        
    def insert(self, ga):
        st = ga.getStatistics()
        stmax = eval_func(ga.bestIndividual())
        stavg = st['fitAve']
        self.fmax.write('{},{}\n'.format(self.i, stmax))
        self.favg.write('{},{}\n'.format(self.i, stavg))
        self.i += 1
        
    def commitAndColse(self, ga):
        self.fmax.close()
        self.favg.close()


configurators = {
    "populationSize": lambda ga, val: ga.setPopulationSize(val),
    "selector": lambda ga, val: ga.selector.set(getattr(pyevolve.Selectors, val))
}

def configure(ga, parameters):
    for name, value in parameters.iteritems():
        if name in configurators:
            configurators[name](ga, value)

def optimize(parameters, outfile_max, outfile_avg):
    genome = pyevolve.G1DBinaryString.G1DBinaryString(64)
    genome.evaluator.set(eval_func)
    ga = pyevolve.GSimpleGA.GSimpleGA(genome)
    
    configure(ga, parameters)
    
    dbadapter = FileAdapter(outfile_max=outfile_max, outfile_avg=outfile_avg, identify="test_run", frequency=1)
    ga.setDBAdapter(dbadapter)
    ga.setGenerations(30)
    ga.evolve(freq_stats=1)
    
    return parse_to_point(ga.bestIndividual())

parameters = eval(open(sys.argv[1]).read())
outfile_max = sys.argv[2]
outfile_avg = sys.argv[3]

optimize(parameters, outfile_max, outfile_avg)
