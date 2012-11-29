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
def parameters_to_id(params):
    text = '.'.join('{}_{}'.format(k, v) for k, v in params)
    return text

def replace_param(params, k, v):
    def toreplace(l):
        if l[0] == k:
            return (k, v)
        return l
    res = map(toreplace, params)
    return res

def plot_all(changingparameters, runner):
    html_report(runner)
    for param in runner.combinations():
        plot_result(param, runner)


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

def links(paramdesc, params):
    l = []
    d = dict(params)
    l.append('<table>\n')
    for p,vals in paramdesc:
        l.append('    <tr><td>{}</td><td>'.format(p))
        lx = []
        for v in vals:
            nparams = replace_param(params, p, v)
            newid = parameters_to_id(nparams)
            if nparams == params:
                lx.append('<strong>{}</strong>'.format(v))
            else:
                lx.append('<a href="#{}">{}</a>'.format(newid, v))
        l.append(' | '.join(lx))
        l.append('</td></tr>\n')
    l.append('</table>\n')
    return ''.join(l)

def all_results(runner):
    allparams = runner.combinations()
    paramdesc = runner.changing_parameters()
    l = []
    for params in allparams:
        path = runner.params_path(params)
        l.append('<a id="{}"></a>\n'.format(parameters_to_id(params)))
        link = links(paramdesc, params)
        img = '<img src="{path}/graph.png">\n<hr>\n'.format(path=path, desc=parameters_to_text(params))
        l.append(link)
        l.append(img)
    return ''.join(l)

def plot_multiple_results(param_list, outfile):
    pass

gnuplot_data_template = """

set terminal pngcairo
set title "{title}"
set output "{output}"
set ylabel 'fitness'
set xlabel 'iteration'
set xrange [-1:]

set datafile separator ','
{plots}

"""

def run_gnuplot(plots, title, output, plotfile):
    
    gnuplot_data = gnuplot_data_template.format(
        plots=plots, 
        title=title,
        output=output,
    )
    
    stdinname = plotfile
    
    with open(stdinname, 'w') as f:
        f.write(gnuplot_data)
    with open(stdinname) as stdinf:
        subprocess.check_call(['gnuplot'], stdin=stdinf)


def plot_result(params, runner):
    datadir = runner.datadir_path(params)
    title = parameters_to_text(params)
    num = len(glob.glob(datadir+'/result*.csv'))
    
    gnuplot_add='plot "{}/summary.csv" w errorbars t "average result" '.format(datadir)
    for i in range(num):
        gnuplot_add += ', "{}/result{}.csv" t "" w l lc "gray"'.format(datadir, i)
    run_gnuplot(
        plots=gnuplot_add,
        plotfile=datadir + '/plot.gpl', 
        output='{}/graph.png'.format(datadir), 
        title=title,
    )
    