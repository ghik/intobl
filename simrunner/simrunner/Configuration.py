class Configuration:
    '''Parameters that we want to override, but not include them in reports'''
    driver='simrunner.pythonplatform'
    binpath='../symulacja/src/main.py'
    repeats=10
    constantParameters = ['algorithm','monitors']
    monitors = ['FileCollector', 'BestFitnessMonitor']
    algorithm = 'EvolutionarySimulation'
    
    '''Parameters for which we will specify several values and for each combination
       of these values a separate simulation will be run'''
    changingParameters = ['agentCount', 'mutationsType']
    
    agentCount = [10, 50]
    mutationsType = ['continuousDistribution', 'normalDistribution']
