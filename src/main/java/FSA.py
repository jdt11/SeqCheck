from jessicat.model import FSAType

""" An FSA object to store the interaction sequence model. """
class FSA(FSAType):
    
    _Q = set()
    _Sigma = set()
    _starting = set()
    _accepting = set()
    _delta = set()
                    
    """ A method which performs the alphabet function. """
    def alphabet(self):
        temp = set()
        for triple in self._delta:
            if type(triple) is tuple:
                temp.add(triple[1])
        return temp
    
    """ A method which converts a specified interaction sequence to an FSA. """
    def seqToFSA(self, sequence):
        sequence = str(sequence).strip()
        if type(sequence) is str and sequence:
            self._Q.add("Initialise")
            self._starting.add("Initialise")
            steps = sequence.split("\n")
            previous = "Initialise"
            for s in steps:
                s = s.strip()
                if s:
                    interaction, widget, num = s.split(" ")
                    num = int(num)
                    self._Q.add(widget)
                    triple = (previous, interaction, widget)
                    self._delta.add(triple)
                    if num > 1:
                        triple = (widget, interaction, widget)
                        self._delta.add(triple)
                    previous = widget
            self._Sigma = self.alphabet()
            self._accepting.add(previous)
    
    """ A method which allows you to determine the states of the automaton from delta. """
    def getStatesFromDelta(self, delta):
        states = set()
        for triple in delta:
            states.add(triple[0])
            states.add(triple[2])
        return states

    """ A method which displays the automaton in the correct format. """
    def __str__(self):
        the_string = "M=(Q,\u03A3,\u03B4,S,F)\n"
        the_string += "Q=" + str(self._Q) + "\n"
        the_string += "\u03A3=" + str(self._Sigma) + "\n"
        the_string += "\u03B4=" + str(self._delta) + "\n"
        the_string += "S=" + str(self._starting) + "\n"
        the_string += "F=" + str(self._accepting) + "\n"
        return the_string
    
    """ A method which displays the string in the correct format for Java. """
    def toString(self):
        return self.__str__()

    """ A method which converts things to the correct format from Java. """
    def javaConversion(self, Q, starting, accepting, delta):
        Q = set(Q)
        starting = set(starting)
        accepting = set(accepting)
        delta_new = set()
        for triple in delta:
            delta_new.add(tuple(triple))
        return Q, starting, accepting, delta_new
    
    """ Initialise an automaton from a sequence. """
    def __init__(self,sequence):
        self._Q = set()
        self._starting = set()
        self._accepting = set()
        self._delta = set()
        self._Sigma = set()
        if sequence:
            self.seqToFSA(sequence)
        
    def getQ(self):
        return self._Q
    
    def getStarting(self):
        return self._starting
    
    def getAccepting(self):
        return self._accepting
    
    def getDelta(self):
        return self._delta
    
    def getSigma(self):
        return self._Sigma
    
    def addState(self, state):
        self._Q.add(state)
        
    def addLetter(self, letter):
        self._Sigma.add(letter)
        
    def addStart(self, state):
        if state in self._Q:
            self._starting.add(state)
            
    def addAccept(self,state):
        if state in self._Q:
            self._accepting.add(state)
    
    def addTriple(self,q,x,qd):
        new_triple = (q,x,qd)
        self._delta.add(new_triple)
        
    def getTriples(self, state):
        triples = set()
        for triple in self._delta:
            if triple[0]==state:
                triples.add(triple)
        return triples
    
    def getWidgets(self):
        new_Q = self._Q.remove("Initialise")
        return list(self._Q)
                