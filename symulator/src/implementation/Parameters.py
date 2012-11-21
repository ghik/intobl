
from implementation.BaseParameters import BaseParameters

class Parameters(BaseParameters):
    algorithm = 'EvolutionarySimulation'
    monitors = ['FileCollector', 'BestFitnessMonitor']
    agentCount = 50
    mutationsType = 'normalDistribution'
