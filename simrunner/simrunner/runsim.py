#!/usr/bin/env python
'''
Created on 07-11-2012

@author: admchr
'''

from simrunner import plotsim, runner

def parse_args(argv):
    import argparse
    argp = argparse.ArgumentParser(prog='runsim', version="0.1",
        description='Run simulation multiple times')
    argp.add_argument('--overwrite', action='store_true', help='force to overwrite earlier simulation data')
    argp.add_argument('script', help='script of the simulation to be run')
    argp.add_argument('dataset', help='name of the resulting dataset')
    return argp.parse_args(argv[1:])

def run(argv):
    args = parse_args(argv)
    
    run = runner.Runner(args.script)
    run.set_dataset(args.dataset, args.overwrite)
    run.run()
