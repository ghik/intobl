from implementation.Parameters import Parameters

class Mutation(object):
    
    def mutate(self, genotype, rand):
        return getattr(self, Parameters.mutation)(genotype, rand)
    
    def continuousDistribution(self, genotype, rand):
        i = rand.randint(0, Parameters.genotypeLength-1)
        mutation = rand.random()
        while mutation > Parameters.mutationFactor:
            mutation = rand.random()
        if rand.randint(0, 100) > 50:
            sign = 1
        else:
            sign = -1
        mutation *= Parameters.mutationMaxValue
        newValue = genotype[i]+sign*mutation
        if newValue > Parameters.cubeSize:
            newValue = Parameters.cubeSize
        if newValue < -Parameters.cubeSize:
            newValue = -Parameters.cubeSize
        genotype[i] = newValue
        return genotype
    
    def normalDistribution(self, genotype, rand):
        i = rand.randint(0, Parameters.genotypeLength-1)
        mutation = rand.normalvariate(0, 1)
        mutation *= Parameters.mutationMaxValue
        if rand.randint(0, 100) > 50:
            sign = 1
        else:
            sign = -1
        newValue = genotype[i] + sign * mutation
  
        if newValue > Parameters.cubeSize:
            newValue = Parameters.cubeSize
        if newValue < -Parameters.cubeSize:
            newValue = -Parameters.cubeSize
        genotype[i] = newValue
        return genotype
        