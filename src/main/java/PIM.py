from jessicat.model import PIMType
import FSA
from xml.dom.minidom import parse
import xml.dom.minidom

""" A class which defines the structure of a well-formed PIM. A PIM is an finite state automaton, and therefore inherits the structure from the FSA class. """
class PIM(PIMType, FSA.FSA):

    #A relation to store the relation between the PIM states and PModels
    _relation = set()
    
    """ A function which gets the domain of the relations. """
    def getStatesFromRelations(self,relations):
        states = set()
        for relation in relations:
            states.add(relation[0])
        return states
    
    """ A method which converts the sets from java to ensure they are in the correct format. """
    def javaConverson(self, relation):
        new_relation = set()
        for r in relation:
            new_relation.add(tuple(r))
        return new_relation

    """ A method which defines how a the PIM is displayed as a string. """
    def __str__(self):
        inherited_string = FSA.FSA.__str__(self)
        the_string = "PI" + inherited_string[0:22] + ",R)\n" + inherited_string[24:]
        the_string += "R=" + str(self._relation) + "\n"
        return the_string
    
    """ A method which allows us to display the string in Java. """
    def toString(self):
        return self.__str__()
    
    """ A method which allows us to process a PIM in an xml file from PIMed. """
    def readFile(self, file):
        DOMTree = xml.dom.minidom.parse(file)
        collection = DOMTree.documentElement
        transitions = collection.getElementsByTagName("transition")
        for t in transitions:
            start = t.getElementsByTagName('start')[0].childNodes[0].data
            end = t.getElementsByTagName('end')[0].childNodes[0].data
            ibeh = t.getElementsByTagName('ibeh')[0].childNodes[0].data
            self._Q.add(start)
            self._Q.add(end)
            triple = (start,ibeh,end)
            self._delta.add(triple)
            relation1 = (start,start)
            relation2 = (end,end)
            self._relation.add(relation1)
            self._relation.add(relation2)
        self._Sigma = FSA.FSA.alphabet(self)
    
    """ A method to initialise a PIM object, we need to read in the PIM from the file specified. """    
    def __init__(self, file):
        self.readFile(file)
        
    """
        Get the next state based on the I-behaviour specified. 
    """
    def getNextState(self,current_state,iBeh):
        for r in self._delta:
            if r[0]==current_state and r[1]==iBeh:
                return r[2]
        return None
    
    """
        Determine if this is a starting state or not. 
    """
    def compareState(self, current_state):
        if current_state in self._starting:
            return 0
        else:
            return 1
    
    """
        Change the starting states. 
    """
    def setStartState(self, state):
        self._starting = set()
        if(state in self._Q):
            self._starting.add(state)
            
    def getQ(self):
        return self._Q