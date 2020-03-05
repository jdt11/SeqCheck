from jessicat.model import PMRType
from xml.dom.minidom import parse
import xml.dom.minidom

""" An object to store the PMR. """
class PMR(PMRType):
    _relation = set()
    
    """ Perform the java conversion to ensure things are in the correct format. """
    def javaConversion(self,relation):
        new_relation = set()
        for r in relation:
            new_relation.add(tuple(r))
        return new_relation
    
    """ A method which allows us to process the PMR in an XML file from PIMed. """
    def readFile(self,file):
        DOMTree = xml.dom.minidom.parse(file)
        collection = DOMTree.documentElement
        relations = collection.getElementsByTagName("rel")
        for r in relations:
            sbeh = r.getElementsByTagName('sbeh')[0].childNodes[0].data
            sop = r.getElementsByTagName('sop')[0].childNodes[0].data
            relation = (sbeh,sop)
            self._relation.add(relation)
        
    """ A method to initialise the PMR object, we need to read the PIM from the file specified. """
    def __init__(self,file):
        self.readFile(file)
        
    """ Display the relations in the correct format. """
    def __str__(self):
        list_relations = list(self._relation)
        the_string = "{" + str(list_relations[0]) + ",\n"
        for r in range(1,len(list_relations)-1):
            the_string += str(list_relations[r]) + ",\n"
        the_string += str(list_relations[-1]) + "}\n"
        return the_string
    
    """ So that the relation can be displayed correctly using the toString method in Java. """
    def toString(self):
        return self.__str__()
    
    """ 
        Get a specific behaviour from the relation. 
    """
    def getBehaviour(self, s_beh):
        for rel in self._relation:
            if rel[0]==s_beh:
                return rel[1]
        return None    
    
    def getAllRelations(self):
        return list(self._relation)
    
    """ 
        Get the domain of the relation i.e. all the S_Behaviours. 
    """
    def getDomain(self):
        domain = list()
        for rel in self._relation:
            domain.append(rel[0])
        return domain
    """ 
        Get the range of the relation i.e. all the specification behaviours.
    """
    def getRange(self):
        range = list()
        for rel in self._relation:
            range.append(rel[1])
        return range
    
