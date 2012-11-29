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

def param_template(tpl, params):
    for t, x in zip(tpl, params):
        if t != x and t[1] != 'all':
            return False
    return True
def is_template(tpl):
    for t in tpl:
        if t[1] == 'all':
            return True
    return False

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
<hr>
{detailed_results}
<div style="height:1000px;width:100px;">.</div>
</body>
</html>
"""

def html_report(runner):
    with open(runner.datadir_root()+'/index.html', 'wt') as f:
        res = html_data_template.format(
            dataset_name=runner.name, 
            date=datetime.datetime.now(),
            samples=runner.config.repeats,
            detailed_results=all_results(runner)+all_summaries(runner))
        f.write(res)

def links(paramdesc, params):
    l = []
    d = dict(params)
    l.append('<table>\n')
    for p,vals in paramdesc:
        l.append('    <tr><td>{}</td><td>'.format(p))
        lx = []
        for v in vals + ['all']:
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

def all_summaries(runner):
    txt=[]
    paramdesc = runner.changing_parameters()
    allparams = list(runner.combinations())
    allparamsex = list(runner.combinations(extend=['all']))
    for params in allparamsex:
        l = []
        for param2 in allparams:
            if param_template(params, param2):
                l.append(param2)
        if not is_template(params):
            continue
        outfile = runner.datadir_root()+'/summary.'+parameters_to_id(params)
        plot_multiple_results(runner, l, outfile)
        txt.append('<a id="{}"></a>\n'.format(parameters_to_id(params)))
        link = links(paramdesc, params)
        img = '<img src="summary.{fname}.png">\n<hr>\n'.format( 
            fname=parameters_to_id(params), 
            desc=parameters_to_text(params))
        txt += [link, img]
    return ''.join(txt)

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

def plot_multiple_results(runner, param_list, outfile):
    gnuplot_add = 'plot '
    pl = []
    for params in param_list:
        datadir = runner.datadir_path(params)
        title = parameters_to_text(params)
        num = len(glob.glob(datadir+'/result*.csv'))
        pl+=['"{}/summary.csv" w errorbars t "{}" '.format(datadir, title)]
    gnuplot_add += ', '.join(pl)
    run_gnuplot(
        plots=gnuplot_add,
        plotfile=outfile+'.gpl', 
        output=outfile+'.png', 
        title='summary',
    )
    

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
    gnuplot_add = 'plot '
    for i in range(num):
        gnuplot_add += '"{}/result{}.csv" t "" w l lc rgb "#888888", '.format(datadir, i)
    gnuplot_add+='"{}/summary.csv" w errorbars t "average result" lc rgb "#ff0000" '.format(datadir)
    run_gnuplot(
        plots=gnuplot_add,
        plotfile=datadir + '/plot.gpl', 
        output='{}/graph.png'.format(datadir), 
        title=title,
    )
    