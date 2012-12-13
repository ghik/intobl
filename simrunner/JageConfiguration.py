
class Configuration:
    driver = 'simrunner.jageplatform'
    execpath = '../jage/algorithms/applications/emas-app'
    outfile = 'results.csv'
    repeats = 3
    
    agexml = "classpath:age.xml"
    dotreplacer = '_'
    
    constantParameters = ['outfile', 'steps', 'problem_size', 'islands_number', 'individual_chanceToMigrate']
    
    steps = 1000
    problem_size = 10
    islands_number = 5
    
    individual_chanceToMigrate = 0.001
    
    changingParameters = ['islands_size', 'feature_chanceToMutate', 'feature_mutationRange']
    
    islands_size = [5, 10, 50]
    feature_chanceToMutate = [0.2, 0.4, 0.6, 0.8]
    feature_mutationRange = [0.0125, 0.025, 0.05, 0.1]
    
