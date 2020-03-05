from jessicat.model import PMODELType
from xml.dom.minidom import parse
import xml.dom.minidom

""" A method which allows you to check if all items in a sequence match a specific type. """
def checkType(items,the_type):
    for i in items:
        if type(i) is not the_type:
            return False
    return True

""" A PModel object which stores the information from the XML file. """
class PModel(PMODELType):
    _name = None
    _CPModels = set()
    _Widgetname = set()
    _Category = set()
    _Behaviour = set()
    
    """ A java conversion method to ensure that things are in the correct format. """
    def javaConversion(self,name,cpmodels):
        name = str(name)
        new_cpmodels = list()
        for cpm in cpmodels:
            new_cpmodels.append(cpm)
        return name, new_cpmodels
    
    """ Get the information for a specific widget. """
    def getWidgetDetails(self, widget):
        behs = set()
        name = str(widget.getElementsByTagName("name")[0].childNodes[0].data)
        cat = str(widget.getElementsByTagName("cat")[0].childNodes[0].data)
        beh = widget.getElementsByTagName("beh")
        if len(beh) > 0:
            for b in beh:
                beh_data = str(b.childNodes[0].data)
                behs.add(beh_data)
            return name, cat, behs
        return name, cat, None
    
    """ Read the information from the PIMed XML file. """
    def readFile(self,file):
        DOMTree = xml.dom.minidom.parse(file)
        collection = DOMTree.documentElement
        pmodel_name = collection.getElementsByTagName("PModel")[0].firstChild.data
        self._name = str(pmodel_name)
        widgets = collection.getElementsByTagName("widget")
        for w in widgets:
            name, cat, beh = self.getWidgetDetails(w)
            if beh:
                self._Behaviour = self._Behaviour.union(beh)
            self._Widgetname.add(name)
            self._Category.add(cat)
        cpmodels = collection.getElementsByTagName("cpmodel")
        for cpm in cpmodels:
            name = cpm.getElementsByTagName('cpname')[0].childNodes[0].data
            cpmodel = CPModel(str(name))
            widgets = cpm.getElementsByTagName("widget")
            for w in widgets:
                name, cat, beh = self.getWidgetDetails(w)
                if beh:
                    decs = (name, cat, tuple(beh))
                else:
                    decs = (name, cat, ())
                cpmodel.addDeclaration(decs)
            self._CPModels.add(cpmodel)
    
    """ Reading the XML file. """
    def __init__(self, file):
        self.readFile(file)

    """ Check if a specific string has a certain character. """
    def stringWith(self,the_list,character):
        the_list = list(the_list)
        if len(the_list) > 0:
            the_string = the_list[0]
            for i in range(1,len(the_list)):
                the_string += character + the_list[i]
            return the_string
        else:
            return ""

    """ Display the PModel in the correct format. """
    def __str__(self):
        format_string = "{:<15}"
        the_string = format_string.format("PModel") + self._name
        cnames = [c._name for c in self._CPModels]
        the_string += " " + self.stringWith(cnames," ") + "\n"
        the_string += format_string.format("Widgetname") + self.stringWith(self._Widgetname," ") + "\n"
        the_string += format_string.format("Category") + self.stringWith(self._Category," ") + "\n"
        the_string += format_string.format("Behaviour") + self.stringWith(self._Behaviour," ") + "\n\n"
        the_string += format_string.format(self._name + " is ") + self.stringWith(cnames," : ") + "\n\n"
        for c in self._CPModels:
            the_string += str(c) + "\n"
        return the_string
    
    """ A method which allows us to display the PModel in Java. """
    def toString(self):
        return self.__str__()
    
    """
        Find a CPModel specified by the name. 
    """
    def findCPModel(self, current_state):
        for cpm in self._CPModels:
            if cpm._name == current_state:
                return cpm
        return None
    
    """ 
        Get the behaviours for a specified CPModel. 
    """
    def getBehaviours(self, widget, current_state):
        cpm = self.findCPModel(current_state)
        if cpm is not None:
        #    print("Found cpm " + cpm._name)
            for dec in cpm._declarations:
                print("dec = " + str(dec))
                print("the widget = " + widget + "; dec[0] = " + str(dec[0]))
                if dec[0]==widget and dec[1] != "Responder":
                    return list(dec[2])
        #else:
        #    print("Not found cpm")
        return None
    
    def getAllBehaviours(self):
        return list(self._Behaviour)
    
    def getAllSBehaviours(self):
        sbehs = list()
        for beh in self._Behaviour:
            if beh[0]=="S":
                sbehs.append(beh)
        return sbehs
    
    def getWidgets(self):
        return list(self._Widgetname)

""" An object which processes a component PModel """
class CPModel(PMODELType):
    
    _name = None
    _declarations = set()
    
    """ A method which converts things from Java into the correct format for Python. """
    def javaConversion(self,name,decs):
        name = str(name)
        new_decs = set()
        for d in decs:
            new_d = list()
            new_t = list()
            for i in range(0,d.size()):
                if i < 2:
                    new_d.append(d[i])
                else:
                    new_t.append(d[i])
            new_d.append(tuple(new_t))
            new_d = tuple(new_d)
            new_decs.add(new_d)
        return name, new_decs

    """ A method which allows us to display the component PModel in the correct format. """
    def __str__(self):
        the_string = self._name + " is\n"
        for d in self._declarations:
            the_string += str(d) + "\n"
        return(the_string)
    
    """ A method which allows us to display the string in Java correctly. """
    def toString(self):
        return self.__str__()
    
    """ A method which initialises the CPM based on a specific name. """
    def __init__(self, name):
        name = str(name)
        if type(name) is str and name:
            self._name = name
            self._declarations = set()
    
    """ A method which allows us to add a declaration to the set of declarations for this CPM."""
    def addDeclaration(self, dec):
        if type(dec) is tuple:
            self._declarations.add(dec)
    