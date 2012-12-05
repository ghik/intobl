class Configuration:
    '''Parameters that we want to override, but not include them in reports'''
    driver='simrunner.pythonplatform'
    binpath='../symulator/src/main.py'
    constantParameters = ['algorithm','monitors']
    monitors = ['FileCollector', 'BestFitnessMonitor']
    algorithm = 'EvolutionarySimulation'
    
    repeats=10
    '''Parameters for which we will specify several values and for each combination
       of these values a separate simulation will be run'''
    changingParameters = ['agentCount', 'mutationsType']
    
    agentCount = [10, 50, 100, 200]
    mutationsType = ['continuousDistribution', 'normalDistribution']
