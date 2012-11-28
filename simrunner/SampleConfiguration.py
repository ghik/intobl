
class Configuration:
    driver='simrunner.sampleplatform'
    binpath='../genetic.py'
    repeats=3
    changingParameters = ['selector', 'populationSize']
    selector = ["GRankSelector", "GRouletteWheel"]
    populationSize = [5, 10, 15]
