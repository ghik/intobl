#!/usr/bin/env python

import glob, sys, subprocess

outname=sys.argv[1]

datadir = 'data/'+outname

gnuplot_data = """

set terminal png
set output "graph.png"
set ylabel 'sss'
set xlabel 'fff'

set datafile separator ','
plot "summary.csv" w errorbars  t 'xyz'

"""
stdinname = datadir + '/plot.gpl'

with open(stdinname, 'w') as f:
    f.write(gnuplot_data)


with open(stdinname) as stdinf:
    subprocess.check_call(['gnuplot'], stdin=stdinf, cwd=datadir)
    
