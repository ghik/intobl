import subprocess, shutil, os, sys

devnull = open('/dev/null', 'w')

paramsFileHeader = """
from implementation.BaseParameters import BaseParameters

class Parameters(BaseParameters):
"""

class Driver:
    def __init__(self, global_configuration):
        self.config = global_configuration
        
    def setup(self):
        pass

    def prepare_parameters(self, changingParams):
        paramsFileContent = paramsFileHeader
        config = self.config
        script = config.binpath
        for param in config.constantParameters:
            paramsFileContent += "    {} = {}\n".format(param, repr(getattr(config, param)))
        
        for param in changingParams:
            (name, value) = param
            paramsFileContent += "    {} = {}\n".format(name, repr(value))
            
        paramsFileName = os.path.join(os.path.dirname(script), 'implementation/Parameters.py')
        paramsFile = open(paramsFileName, 'w')
        paramsFile.write(paramsFileContent)
        paramsFile.close()
    
    def run(self, datadir, parameters):
        
        script = self.config.binpath 
        
        if not os.path.exists(script):
            print 'Could not find script {}'.format(script)
            sys.exit(1)
        for i in range(self.config.repeats):
            subprocess.check_call([script], stdout=devnull)
            shutil.move('./stats.txt', datadir+'/result{}.csv'.format(i))
    