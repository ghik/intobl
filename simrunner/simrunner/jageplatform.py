import os, subprocess, sys, csv

devnull = open('/dev/null', 'w')

class Driver:
    def __init__(self, global_configuration):
        self.config = global_configuration
        self.propertiesPath = '/tmp/age.properties'
        self.outputs = dict(maxfitness='maxfitness', avgfitness='avgfitness')
        
    def setup(self):
        execpath = self.config.execpath
        
        cwd = os.getcwd()
        os.chdir(execpath)
        
        if not os.path.exists('pom.xml'):
            print 'Could not find pom.xml in {}'.format(execpath)
            sys.exit(1)
            
        subprocess.check_call(['mvn', 'package', 'dependency:copy-dependencies'], stdout=devnull)
        os.chdir(cwd)
        
    def prepare_parameters(self, changingParams):
        config = self.config
        dotreplacer = config.dotreplacer
        
        propertiesFileContent = ""
        for param in config.constantParameters:
            propertiesFileContent += "{} = {}\n".format(param.replace(dotreplacer, '.'), getattr(config, param))
        
        for param in changingParams:
            (name, value) = param
            propertiesFileContent += "{} = {}\n".format(name.replace(dotreplacer, '.'), value)
            
        propertiesFile = open(self.propertiesPath, 'w')
        propertiesFile.write(propertiesFileContent)
        propertiesFile.close()
    
    def decode_output(self, outfile, datadir, i):
        keys = 'maxfitness,avgfitness,avgenergy'.split(',')
        files = {}
        for key in keys:
            files[key] = open(os.path.join(datadir, 'result.{}.{}.csv'.format(key, i)), 'wt')
        
        with open(outfile, 'rt') as f:
            results = csv.reader(f)
            fieldnames = results.next() 
            assert fieldnames == 'steps,bestFitnessEver,avgFitness,avgChildEnergy'.split(','), fieldnames
            for r in results:
                res = {}
                num, res['maxfitness'], res['avgfitness'], res['avgenergy'] = r
                for key in keys:
                    files[key].write('{},{}\n'.format(num, res[key]))
        for key in keys:
            files[key].close() 
    
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
                                   '-Dage.node.conf=' + agexml])#, stdout=devnull)
            
            self.decode_output(outfile, os.path.join(cwd, datadir), i)
            
        os.chdir(cwd)
    
