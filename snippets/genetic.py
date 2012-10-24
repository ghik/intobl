import pyevolve
import pyevolve.G1DBinaryString
import pyevolve.GSimpleGA

import numpy

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

genome = pyevolve.G1DBinaryString.G1DBinaryString(64)
genome.evaluator.set(eval_func)
ga = pyevolve.GSimpleGA.GSimpleGA(genome)

dbadapter = pyevolve.DBAdapters.DBFileCSV(filename="results.csv",identify="test_run",frequency=1,reset=True)
ga.setDBAdapter(dbadapter)

ga.evolve(freq_stats=1)
print parse_to_point(ga.bestIndividual())

