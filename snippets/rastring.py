import numpy
import matplotlib.pyplot as plt

from scipy.optimize import anneal, fmin
def rastring(x):
    n = len(x)
    A = 10
    return A*n+sum(x**2 - A*numpy.cos(2*numpy.pi*x))

ar = numpy.array
    
p0 = ar([4.5, 3,])
print 'before optimization:'
print p0, rastring(p0)

print 'annealing:'
ret = anneal(rastring, p0, schedule='boltzmann', maxiter=1000)
print ret[0], rastring(ret[0])
print 'simplex:'
ret = fmin(rastring, p0, maxiter=1000)
print ret, rastring(ret)

if not False:
    xs = numpy.arange(-5, 5, 0.1)
    xs1, xs2 = numpy.meshgrid(xs, xs)
    ys = numpy.array([rastring(ar(x)) for x in zip(xs1, xs2)])
    plt.pcolor(xs1, xs2, ys)
    plt.grid()
    plt.show()
