#!/usr/bin/env python
'''
Created on 07-11-2012

@author: admchr
'''

import glob
import subprocess, datetime


def parameters_to_text(params):
    text = ' '.join('{}={}'.format(k, v) for k, v in params)
    return text


def plot_results(parameterspace, changingparameters, runner):
    html_report(runner)
    for param in parameterspace:
        plot_result(param, runner.params_to_path)


html_data_template = """
<!DOCTYPE html>
<html>
<head>
</head>
<body>
<table>
<h1>simulation run: {dataset_name}</h1>
<tr><td>date:</td><td>{date}</td></tr>
<tr><td>sample size:</td><td>{samples}</td></tr>
</table>
{detailed_results}
</body>
</html>
"""

def html_report(runner):
    with open(runner.datadir_root()+'/index.html', 'wt') as f:
        res = html_data_template.format(
            dataset_name=runner.name, 
            date=datetime.datetime.now(),
            samples=runner.config.repeats,
            detailed_results=all_results(runner))
        f.write(res)

def all_results(runner):
    allparams = runner.combinations()
    l = []
    for params in allparams:
        path = runner.params_path(params)
        img='<img src="{path}/graph.png">\n'.format(path=path)
        l.append(img)
    return ''.join(l)

gnuplot_data_template = """

set terminal pngcairo
set title "{title}"
set output "graph.png"
set ylabel 'fitness'
set xlabel 'iteration'
set xrange [-1:]

set datafile separator ','
plot "summary.csv" w errorbars t "average result" {detail_plots}

"""

def plot_result(params, params_to_path):
    datadir = params_to_path(params)
    title = parameters_to_text(params)
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
