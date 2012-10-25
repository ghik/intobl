#!/usr/bin/env python

import glob, sys, subprocess, shutil

outname=sys.argv[1]

datadir = 'data/'+outname

gnuplot_data = """

set terminal png
set title "{title}"
set output "graph.png"
set ylabel 'fitness'
set xlabel 'iteration'
set xrange [-1:]

set datafile separator ','
plot "summary.csv" w errorbars t "average result" {detail_plots}

"""

num = len(glob.glob(datadir+'/result*.csv'))

gnuplot_add=''
for i in range(num):
    gnuplot_add += ', "result{}.csv" t "" w l lc "gray"'.format(i)

gnuplot_data = gnuplot_data.format(detail_plots=gnuplot_add, title='fitness for dataset '+outname)

stdinname = datadir + '/plot.gpl'

with open(stdinname, 'w') as f:
    f.write(gnuplot_data)


with open(stdinname) as stdinf:
    subprocess.check_call(['gnuplot'], stdin=stdinf, cwd=datadir)

shutil.copyfile(datadir+'/graph.png', 'graph.png')
