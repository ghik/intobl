#!/usr/bin/env python
'''
Created on 07-11-2012

@author: admchr
'''

import glob
import subprocess, datetime, os.path

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

def expand_template(runner, params):
    l = []
    for param2 in runner.combinations():
        if param_template(params, param2):
            l.append(param2)
    return l

def all_templates(runner):
    for t in runner.combinations(extend=['all']):
        if is_template(t):
            yield t

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
    for param in all_templates(runner):
        plot_multiple_results(runner, param)

html_data_template = """
<!DOCTYPE html>
<html>
<head>
</head>
<body>
<h1>simulation run: {dataset_name}</h1>
<table>
<tr><td>date:</td><td>{date:%Y-%m-%d %H:%M:%S}</td></tr>
<tr><td>sample size:</td><td>{samples}</td></tr>
<tr><td>common parameters:</td><td>{parameters}</td></tr>
</table>
<a href="../../index.html">Dataset list</a>
<hr>
{detailed_results}
<div style="height:800px;width:100px;">.</div>
</body>
</html>
"""

def html_report(runner):
    now = datetime.datetime.now()
    with open(runner.datadir_root() + '/desc.py', 'wt') as f:
        f.write(str(dict(name=runner.name, date=now, samples=runner.config.repeats)))
    with open(runner.datadir_root() + '/index.html', 'wt') as f:
        res = html_data_template.format(
            dataset_name=runner.name,
            date=now,
            samples=runner.config.repeats,
            detailed_results=all_results(runner) + all_summaries(runner),
            parameters=parameters_to_text(runner.constant_parameters),
        )
        f.write(res)
    html_update_master_page(runner)

html_master_template = """
<!DOCTYPE html>
<html>
<head>
<style type="text/css">

th {{
    background: #00f;
    color: #fff;
}}
th, td {{
    padding: 2px;
}}
tr:nth-child(even) {{
    background: #ddd;
}}
</style> 
</head>
<body>
<h1>Simulation runs</h1>
<hr>
{all_runs}
</body>
</html>
"""
def html_update_master_page(runner):
    with open(runner.global_root() + '/index.html', 'wt') as f:
        txt = html_master_template.format(all_runs=dataset_list(runner))
        f.write(txt)

def dataset_list(runner):
    txt = []
    txt.append('<table>\n')
    txt.append('<tr><th>dataset name</th><th>date</th></tr>')
    for d in sorted(glob.glob(runner.datadir_global_root() + '/*')):
        fpath = d + '/desc.py'
        if os.path.isfile(fpath):
            with open(fpath, 'rt') as f:
                desc = eval(f.read())
                index = d + '/index.html'
                txt.append('<tr><td><a href="{index}">{name}</a></td><td>{date:%Y-%m-%d %H:%M}</td></tr>\n'.format
                           (index=index, **desc))
    txt.append('</table>')
    return ''.join(txt)

def links(paramdesc, params):
    l = []
    d = dict(params)
    l.append('<table>\n')
    for p, vals in paramdesc:
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
    txt = []
    paramdesc = runner.changing_parameters
    for params in all_templates(runner):
        txt.append('<a id="{}"></a>\n'.format(parameters_to_id(params)))
        link = links(paramdesc, params)
        txt.append(link)
        for result in runner.outputs:
            img = '<a href="summary.{datatype}.{fname}.pdf"><img src="summary.{datatype}.{fname}.png"></a>\n'.format(
                fname=parameters_to_id(params),
                datatype=result
            )
            txt.append(img)
        txt.append('<div>[gnuplot files: ')
        for result in runner.outputs:
            img = '<a href="summary.{datatype}.{fname}.gpl">{label}</a> '.format(
                fname=parameters_to_id(params),
                label=runner.outputs[result],
                datatype=result
            )
            txt.append(img)
        img = ']</div><hr>\n'
        txt.append(img)
    return ''.join(txt)

def all_results(runner):
    allparams = runner.combinations()
    paramdesc = runner.changing_parameters
    txt = []
    for params in allparams:
        path = runner.params_path(params)
        txt.append('<a id="{}"></a>\n'.format(parameters_to_id(params)))
        link = links(paramdesc, params)
        txt.append(link)
        for result in runner.outputs:
            img = '<a href="{path}/graph.{datatype}.pdf"><img src="{path}/graph.{datatype}.png"></a>\n'.format(
                path=path,
                datatype=result
            )
            txt.append(img)
        txt.append('<div>[gnuplot files: ')
        for result in runner.outputs:
            img = '<a href="{path}/graph.{datatype}.gpl">{label}</a> '.format(
                path=path,
                label=runner.outputs[result],
                datatype=result
            )
            txt.append(img)
        img = '] | <a href="{path}/">[data folder]</a></div><hr>\n'.format(
            path=path
        )
        txt.append(img)
    return ''.join(txt)

def plot_multiple_results(runner, template):
    for result in runner.outputs:
        outfile = '{}/summary.{}.{}'.format(runner.datadir_root(), result, parameters_to_id(template))
        param_list = expand_template(runner, template)
        gnuplot_add = 'plot '
        pl = []
        for params in param_list:
            datadir = runner.datadir_path(params)
            title = parameters_to_text(params)
            pl += ['"{}/summary.{}.csv" w errorbars t "{}" '.format(datadir, result, title)]
        gnuplot_add += ', '.join(pl)
        run_gnuplot(
            plots=gnuplot_add,
            output=outfile,
            title='summary',
            label=runner.outputs[result]
        )
    

gnuplot_data_template = """

set title "{title}"
set ylabel '{label}'
set xlabel 'iteration'
set xrange [-1:]

set datafile separator ','

set terminal pngcairo
set output "{output}.png"
{plots}

set terminal pdfcairo
set output "{output}.pdf"
{plots}

"""

def run_gnuplot(plots, title, output, label):
    gnuplot_data = gnuplot_data_template.format(
        plots=plots,
        title=title,
        output=output,
        label=label,
    )
    
    stdinname = output + '.gpl'
    with open(stdinname, 'w') as f:
        f.write(gnuplot_data)
    with open(stdinname) as stdinf:
        subprocess.check_call(['gnuplot'], stdin=stdinf)

def plot_result(params, runner):
    for result in runner.outputs:
        datadir = runner.datadir_path(params)
        title = parameters_to_text(params)
        num = len(glob.glob('{datadir}/result.{datatype}.*.csv'.format(datadir=datadir, datatype=result)))
        gnuplot_add = 'plot '
        for i in range(num):
            gnuplot_add += '"{datadir}/result.{datatype}.{i}.csv" t "" w l lc rgb "#888888", '.format(datadir=datadir, i=i, datatype=result)
        gnuplot_add += '"{datadir}/summary.{datatype}.csv" w errorbars t "average result" lc rgb "#ff0000" '.format(datadir=datadir, datatype=result)
        run_gnuplot(
            plots=gnuplot_add,
            output='{datadir}/graph.{datatype}'.format(datadir=datadir, datatype=result),
            title=title,
            label=runner.outputs[result]
        )
