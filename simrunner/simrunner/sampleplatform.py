import subprocess, shutil, os, sys, tempfile

devnull = open('/dev/null', 'w')
class Driver:
    def __init__(self, global_configuration):
        self.config = global_configuration
        
    def setup(self):
        pass

    def prepare_parameters(self, changingParams):
        self.params = changingParams
    
    def run(self, datadir, parameters):
        with tempfile.NamedTemporaryFile('wt') as f:
            script = self.config.binpath
            txtparam = repr(dict(parameters))
            f.write(txtparam)
            f.flush()
            if not os.path.exists(script):
                print 'Could not find script {}'.format(script)
                sys.exit(1)
            for i in range(self.config.repeats):
                outfile = datadir + '/result.fitness.{}.csv'.format(i)
                paramfile = f.name
                subprocess.check_call([script, paramfile, outfile], stdout=devnull)

