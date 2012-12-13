import os
import shutil
import subprocess
import sys

devnull = open('/dev/null', 'w')

class Driver:
    def __init__(self, global_configuration):
        self.config = global_configuration
        self.propertiesPath = '/tmp/age.properties'
        
    def setup(self):
        execpath = self.config.execpath
        
        cwd = os.getcwd()
        os.chdir(execpath)
        
        if not os.path.exists('pom.xml'):
            print 'Could not find pom.xml in {}'.format(execpath)
            sys.exit(1)
            
        subprocess.check_call(['mvn', 'package', 'dependency:copy-dependencies'], stdout=devnull)
        os.chdir(cwd)
        
    def prepare_parameters(self, params):
        config = self.config
        dotreplacer = config.dotreplacer
        
        propertiesFileContent = ""
        for param in config.constantParameters:
            propertiesFileContent += "{} = {}\n".format(param.replace(dotreplacer, '.'), getattr(config, param))
        
        for param in params:
            (name, value) = param
            propertiesFileContent += "{} = {}\n".format(name.replace(dotreplacer, '.'), value)
            
        propertiesFile = open(self.propertiesPath, 'w')
        propertiesFile.write(propertiesFileContent)
        propertiesFile.close()
    
    def run(self, datadir, parameters):
        agexml = self.config.agexml
        execpath = self.config.execpath
        outfile = self.config.outfile

        cwd = os.getcwd()
        os.chdir(execpath)
            
        for i in range(self.config.repeats):
            subprocess.check_call(['java', '-cp', 'target/*:target/dependency/*',
                                   '-Dage.config.properties=' + self.propertiesPath,
                                   'org.jage.platform.cli.CliNodeBootstrapper',
                                   '-Dage.node.conf=' + agexml])
            
            shutil.move(outfile, os.path.join(cwd, datadir, 'result{}.csv'.format(i)))
            
        os.chdir(cwd)
    
