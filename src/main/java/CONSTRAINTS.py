from jessicat.model import CONSTRAINTType

class Constraint(CONSTRAINTType):
    _name = ""
    _start_observations = set()
    _sequence = list()
    _end_observations = set()

    def __init__(self,name):
        if name:
            self._name = name
        self._start_observations = set()
        self._sequence = list()
        self._end_observations = set()

    def getName(self):
        return self._name
    
    def getStartObservations(self):
        return self._start_observations
    
    def getSequence(self):
        return self._sequence
    
    def getEndObservations(self):
        return self._end_observations
    
    """ 
        Find a specific observation based on its name. 
    """
    def findObservation(self,obs,obs_name):
        if type(obs) is set and obs and obs_name:
            for t in obs:
                if t[0]==obs_name:
                    return t
        return None

    """
        Add an observation to either the starting or ending assumptions based on the indicator. True 
        stands for the start observations and false for the end observations. 
    """
    def addObservation(self, obs_name, obs_value, indicator):
        if obs_name and obs_value:
            inStart =  self.findObservation(self._start_observations, obs_name)
            inEnd = self.findObservation(self._end_observations, obs_name)
            if indicator and inStart==None:
                self._start_observations.add((obs_name,obs_value))
            elif indicator and inStart!=None:
                self.modifyObservation((obs_name,obs_value), indicator)
            elif inEnd==None:
                self._end_observations.add((obs_name,obs_value))
            elif inEnd!=None:
                self.modifyObservation((obs_name,obs_value), indicator)

    """
        Remove an observation from either the start or end observations based on the indicator.
    """
    def removeObservation(self,obs,indicator):
        if type(obs) is tuple and obs:
            if indicator:
                self._start_observations.remove(obs)
            else:
                self._end_observations.remove(obs)

    def setSequence(self,seq):
        if seq:
            self._sequence = seq

    """
        Modify the value of an observation. 
    """
    def modifyObservation(self,obs,indicator):
        if type(obs) is tuple and obs:
            obs_name = obs[0]
            if indicator:
                old_obs = self.findObservation(self._start_observations,obs_name)
                self.removeObservation(old_obs,True)
                self.addObservation(obs[0],obs[1],True)
            else:
                old_obs = self.findObservation(self._end_observations,obs_name)
                self.removeObservation(old_obs,False)
                self.addObservation(obs[0],obs[1],False)

    def toString(self):
        the_string = ""
        the_string += "Constraint: " + self._name + "\n"
        the_string += "Sequence: " + str(self._sequence) + "\n"
        the_string += "Start Observations:\n"
        for obs in self._start_observations:
            the_string += "\t" + str(obs) + "\n"
        the_string += "End Observations:\n"
        for obs in self._end_observations:
            the_string += "\t" + str(obs) + "\n"
        return the_string

    def __str__(self):
        return self.toString()
    
    """
        Reset the observations. 
    """
    def removeObservations(self):
        self._start_observations = set()
        self._end_observations = set()




