from agent.Agent import Agent
from implementation.Parameters import *
from mutation.Mutation import Mutation
from random import Random
import math

class Specimen(Agent):

    def __init__(self, id, env):
        Agent.__init__(self, id, env)
        self._energy = Parameters.initEnergy
        self._reproductionMinEnergy = Parameters.reproductionMinEnergy
        self._genotype = []
        self._rand = Random()
        rand = self._rand
        for i in xrange(Parameters.genotypeLength):
            if rand.randint(0, 100) > 0:
                self._genotype.append(Parameters.cubeSize * rand.random())
            else:
                self._genotype.append(-1 * Parameters.cubeSize * rand.random())
    
    def getFitness(self):
        return getattr(self, "_get"+Parameters.function+"Fitness")()
    
    def _getRastriginFitness(self):
        result = 0
        val = 2 * math.pi
        for el in self._genotype:
            result += (el * el - 10 * math.cos(val * el))
        result += 10 * len(self._genotype)
        return result

    def _getRosenbrockFitness(self):
        assert Parameters.genotypeLength == 2
        x = self._genotype[0]
        y = self._genotype[1]
        return math.pow(1-x, 2) + 100*pow(y - x*x, 2)
    
    def _getAckleyaFitness(self):
        genSum = 0
        cos_sum = 0
        val = 2*math.pi
        for x_i in self._genotype:
            genSum += x_i * x_i
            cos_sum += math.cos(val*x_i)
        return -20*math.exp(-0.2*math.sqrt(genSum/Parameters.genotypeLength))-math.exp(cos_sum/Parameters.genotypeLength)+20+math.exp(1)

    def cosinus(self, specimen2):
        vec1, vec2 = self.getGenotype(), specimen2.getGenotype()
        sumxy = float(sum([vec1[key] * vec2[key] for key in range(len(vec1))]))
        if sumxy == 0.0:
            return 0.0
        sumx = float(sum([vec1[key] ** 2 for key in range(len(vec1))]))
        sumy = float(sum([vec2[key] ** 2 for key in range(len(vec1))]))
        return sumxy / (math.sqrt(sumx) * math.sqrt(sumy))

    def wantToReproduce(self):
        if self._energy >= self._reproductionMinEnergy:
            if self._energy - Parameters.initEnergy / 2 > 0:
                return True
        return False
    
    def wantToMigrate(self):
        if self._rand.random() <= Parameters.migrationProbability:
            return True
        return False
    
    def getEnergy(self):
        return self._energy
    
    def setEnergy(self, energy):
        self._energy = energy
    
    def setAfterReproductionEnergy(self):
        self._energy -= Parameters.initEnergy/2
        
    def rollbackReproductionEnergy(self):
        self._energy += Parameters.initEnergy/2
        
    def getGenotype(self):
        return self._genotype
    
    def setNewGenotype(self, gen1, gen2):
        newGenotype = []
        #for i in range(Parameters.genotypeLength):
        #    newGenotype += [(gen1[i] + gen2[i]) / 2]
        part = self._rand.randint(0, Parameters.genotypeLength)
        for i in xrange(part):
            newGenotype += [gen1[i]]
        for i in xrange(part, Parameters.genotypeLength):
            newGenotype += [gen2[i]]
        self._genotype=Mutation().mutate(newGenotype, self._rand)
    
    def fight(self, otherAgent):
        myFitness = self.getFitness()
        otherAgentFitness = otherAgent.getFitness()
        if myFitness <= otherAgentFitness:
            self._energy += Parameters.fightEnergyWin
            otherAgent._energy += Parameters.fightEnergyLoose
            self._checkIfKill(otherAgent, self)
        else:
            otherAgent._energy += Parameters.fightEnergyWin
            self._energy += Parameters.fightEnergyLoose
            self._checkIfKill(self, otherAgent)
        
    def _checkIfKill(self, looser, winner):
        if looser._energy <= Parameters.dieEnergy:
            winnerEnergy = winner.getEnergy()
            winnerEnergy -= (Parameters.dieEnergy - looser._energy)
            winner.setEnergy(winnerEnergy)
            looser.kill()
    
    def movePossibilities(self):
        return self.getEnv().getFreeFields(self)
    
    def wantToMeet(self):
        value = self._rand.random()
        if value <= Parameters.meetingProbability:
            return True
        return False