import os
import shutil
import subprocess
import sys

devnull = open('/dev/null', 'w')

paramsFileHeader = """
from implementation.BaseParameters import BaseParameters

class Parameters(BaseParameters):
"""

class Driver:
    def __init__(self, global_configuration):
        self.config = global_configuration
        self.propertiesPath = '/tmp/age.properties'
        
    def prepare_parameters(self, params):
        config = self.config
        
        propertiesFileContent = ""
        for param in config.constantParameters:
            propertiesFileContent += "{} = {}\n".format(param, getattr(config, param))
        
        for param in params:
            (name, value) = param
            propertiesFileContent += "{} = {}\n".format(name, value)
            
        propertiesFile = open(self.propertiesPath, 'w')
        propertiesFile.write(propertiesFileContent)
        propertiesFile.close()
    
    def run(self, datadir, parameters):
        agexml = self.config.agexml
        execpath = self.config.execpath

        cwd = os.getcwd()
        os.chdir(execpath)
        
        if not os.path.exists('pom.xml'):
            print 'Could not find pom.xml in {}'.format(execpath)
            sys.exit(1)
        for i in range(self.config.repeats):
            subprocess.check_call(['mvn', 'exec:java', '-Dage.node.conf=' + agexml,
                                   '-Dage.config.properties=' + self.propertiesPath], stdout=devnull)
            # shutil.move('./stats.txt', datadir + '/result{}.csv'.format(i))
            
        os.chdir(cwd)
    
