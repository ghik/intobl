
class BaseParameters(object):
    algorithms = ['GeneticSimulation', 'EvolutionarySimulation']
    algorithm = algorithms[1]

    mutationsType = ['continuousDistribution', 'normalDistribution']

    simSteps = 100
    agentSteps = None
    
    showOutput = False
    printStats = False
    printStatsGlobal = True
    printHerdStats = False
    drawCharts = False
    
    '''How many simulations to launch consecutively.'''
    simulations = 1
    
    '''In our example it means, that they are 3 normalized float values (RGB)'''
    genotypeLength = 50
    #genotypeLength = 2
    
    '''Monitors to use'''
    monitors = [
                #'AgentStepsCountMonitor',            # GEN -
                #'CenterOfGravityMonitor',            # GEN -
                #'CenterOfGravityMoveMonitor',        # -   -
                #'DieAndReproductionMonitor',         # GEN -
                #'DistFromCoGMonitor',                # -   -
                #'PopulationCountMonitor',            # GEN -
                #'ReadyToReproductionMonitor',        # GEN -
                'ResultMonitor',                     # GEN EVOL
                #'SimStepsToMakeResultBetterMonitor', # GEN - 
                'StatsCollector',                    # GEN EVOL
                'DiversityMonitor'                   # GEN EVOL
                ]
    
    actionMonitors = [
                #'ReproductionFailMonitor',           # GEN - 
                #'SimilarityReproductionMonitor',     # GEN -
                ]
    
    '''Fitness function'''
    function = 'Rastrigin'
    #function = 'Rosenbrock'
    #function = 'Ackleya'
    
    '''Determines size of space where we search for result. For example if cubeSize=a, 
       then coordinates is from [-a, a], so it creates a cube with lines of length equals 2a
    '''
    cubeSize = 10
    
    agentCount = 50
    herdAgentsCount = 2
    
    '''Has to be divisible by 2'''
    initEnergy = 100
    
    fieldSize = 10
    
    reproductionMinEnergy = 90
    
    fightEnergyWin = 20
    fightEnergyLoose = -20
    
    dieEnergy = 0
    
    meetingProbability = 1
    
    '''Configuration of mutations'''
    mutation = mutationsType[1]
    
    '''Migration probability [0.0 - 1.0]'''
    migrationProbability = 0.01
    
    '''Used only in continuousDistribution. It has to be float (0,1)'''
    mutationFactor = [0.1]
    
    '''Normally mutation value is from [0,1], by it will by multiply by mutationMaxValue'''
    mutationMaxValue = 1

    '''Number of timestamps, after which stats are written into file'''
    statsCollectFreq = 20
    
    
    ''' ############################# Evolutionary alg params ################### '''
    matingPoolSize = 8
    bestNumber = 0
