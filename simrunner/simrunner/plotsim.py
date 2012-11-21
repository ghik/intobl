#!/usr/bin/env python
'''
Created on 07-11-2012

@author: admchr
'''

import glob
import subprocess

gnuplot_data_template = """

set terminal png
set title "{title}"
set output "graph.png"
set ylabel 'fitness'
set xlabel 'iteration'
set xrange [-1:]

set datafile separator ','
plot "summary.csv" w errorbars t "average result" {detail_plots}

"""

def plot_results(datadir, title):
    num = len(glob.glob(datadir+'/result*.csv'))
    
    gnuplot_add=''
    for i in range(num):
        gnuplot_add += ', "result{}.csv" t "" w l lc "gray"'.format(i)
    
    
    gnuplot_data = gnuplot_data_template.format(detail_plots=gnuplot_add, title=title)
    
    stdinname = datadir + '/plot.gpl'
    
    with open(stdinname, 'w') as f:
        f.write(gnuplot_data)
    
    with open(stdinname) as stdinf:
        subprocess.check_call(['gnuplot'], stdin=stdinf, cwd=datadir)
