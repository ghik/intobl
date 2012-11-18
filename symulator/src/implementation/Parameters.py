
from implementation.BaseParameters import BaseParameters

class Parameters(BaseParameters):
    algorithm = 'EvolutionarySimulation'
    monitors = ['FileCollector', 'BestFitnessMonitor']
    agentCount = 200
    mutationsType = 'normalDistribution'
