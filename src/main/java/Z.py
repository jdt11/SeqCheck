from jessicat.model import ZType
import random

""" The Manager for the Z specification """
class Manager(ZType):
    
    _the_filename = ""
    _name = ""
    _schema_names = set()
    _obs_relation = set()
    _lines = []

    """ This initialises the Z specification, getting the useful information from the Z spec. """
    def __init__(self, file, name):
        file = str(file)
        name = str(name)
        if len(file) > 0 and len(name) > 0:
            self._the_filename = file
            self.readFile()
            self.getSchemaNames()
            self._name = name
            self.getInitObservations()

    """ Open the Z file for reading. """
    def readFile(self):
        f = open(self._the_filename, "r")
        self._lines = f.readlines()
        for i in range(0,len(self._lines)):
            self._lines[i] = self._lines[i].strip("\n")

    """ Find the names of all the schemas. """
    def getSchemaNames(self):
        for line in self._lines:
            if "\\begin" in line and "schema" in line:
                self._schema_names.add(line[15:-1])
        return list(self._schema_names)

    """ Get the starting observation values. """
    def getInitObservations(self):
        for i in range(0,len(self._lines)):
            init_schema = "\\begin{schema}{Init}"
            if init_schema in self._lines[i]:
                i += 1
                line = self._lines[i]
                while "\\end{schema}" not in line:
                    info = line.split("=")
                    if len(info) >= 2:
                        obs_name = info[0].strip()
                        obs_value = info[1].strip().strip("\\\\")
                        self._obs_relation.add((obs_name,obs_value))
                
                    i += 1
                    line = self._lines[i]
    
    """ Display the Z specification information. """                
    def __str__(self):
        the_string = "Z Manager\n"
        the_string += "Filename: " + str(self._the_filename) + "\n"
        the_string += "Interactive System name:" + str(self._name) + "\n"
        the_string += "Schema names:" + str(self._schema_names) + "\n"
        the_string += "Observations:" + str(self._obs_relation) + "\n"
        return the_string
    
    """ A mapper method for the java toString() method. """
    def toString(self):
        return self.__str__()
    
    """ Get a random set of schemas to process using the ProB2 Tooling Template. """
    def getRandomSchemas(self, number):
        names = []
        temp = list(self._schema_names)
        for i in range(0,number):
            names.append(random.choice(temp))
        return names
    
    def getSystemName(self):
        return self._name;
    
    def getObservations(self):
        return self._obs_relation






