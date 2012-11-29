
class Configuration:
    driver = 'simrunner.jageplatform'
    execpath = '../../jage/algorithms/applications/vanilla-genetic'
    repeats = 3
    
    agexml = "classpath:age.xml"
    dotreplacer = '_'
    
    constantParameters = ['steps', 'problem_size', 'feature_chanceToMutate',
                          'feature_mutationRange', 'individual_chanceToRecombine']
    
    steps = 1000
    problem_size = 10
    
    individual_chanceToMutate = 0.5
    
    feature_chanceToMutate = 0.5
    feature_mutationRange = 0.01
    
    changingParameters = ['population_size', 'individual_chanceToRecombine']
    
    population_size = [10, 20, 30, 40, 50]
    individual_chanceToRecombine = [0.2, 0.4, 0.6, 0.8, 1.0]
    

    
