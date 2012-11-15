from simulation.SimLogic import SimLogic
from implementation.Parameters import Parameters
from monitor.StatsMonitor import StatsMonitor
from implementation.HerdAgent import HerdAgent

class FileCollector(StatsMonitor):
    
    def __init__(self, agent_class,filename='stats.txt'):
        StatsMonitor.__init__(self, agent_class)
        self._it = 0
        self._filename = filename
            
    def getParametersNames(self):
        return ['fitnesses']
    
    def printAgregatedValue(self):
        pass
      
    def printStats(self, params):
        pass
    
    def actualStats(self, stats):
        fitnesses = stats['fitnesses']
        lx = []
        for l in fitnesses:
            lx.extend(l)
        v = max(lx)
        with open(self._filename, 'a') as f:
            f.write('{},{}\n'.format(self._it, v))
        self._it+=1
