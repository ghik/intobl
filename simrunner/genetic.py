#!/usr/bin/env python
'''
Created on 07-11-2012

@author: ghik
'''

from pyevolve.DBAdapters import DBFileCSV as CSVAdapter
import numpy
import pyevolve.G1DBinaryString
import pyevolve.GSimpleGA
import sys

def rastrigin(x):
    n = len(x)
    A = 10
    return A*n+sum(x**2 - A*numpy.cos(2*numpy.pi*x))

def parse_to_point(binary_string,minx=-5.0,miny=-5.0,maxx=5.0,maxy=5.0):
    dec = binary_string.getDecimal()
    x = minx + ((dec / 2**32) / float(2**32)) * (maxx-minx)
    y = miny + ((dec % 2**32) / float(2**32)) * (maxy-miny)
    return [x,y]

def eval_func(individual):
    return 100.0 - rastrigin(numpy.array(parse_to_point(individual)))
   
class CustomizedCSVAdapter(CSVAdapter):
    def __init__(self,**args): 
        CSVAdapter.__init__(self,**args)
        self.i = 0
    def insert(self,ga):
        self.fHandle.write(str(self.i)+","+str(eval_func(ga.bestIndividual()))+"\n")
        self.i += 1 

def optimize(parameters, outfile):
    genome = pyevolve.G1DBinaryString.G1DBinaryString(64)
    genome.evaluator.set(eval_func)
    ga = pyevolve.GSimpleGA.GSimpleGA(genome)
    
    dbadapter = CustomizedCSVAdapter(filename=outfile,identify="test_run",frequency=1,reset=True)
    ga.setDBAdapter(dbadapter)
    ga.setGenerations(30) 
    ga.evolve(freq_stats=1)
    
    return parse_to_point(ga.bestIndividual())

parameters = eval(open(sys.argv[1]).read())
outfile = sys.argv[2]

optimize(parameters, outfile)