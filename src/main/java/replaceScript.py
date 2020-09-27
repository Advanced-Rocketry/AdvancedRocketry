# Replaces an != comparison with !.equals()

from pathlib import Path
import re

# edit this
constantToReplace = 'ItemStationChip\.getUUID\(\w+\)'

# Match a variable name or chain of function calls
nePattern = '(?:(?:\w+(?:\(\))?)(?:\.\w+(?:\(\))?)+ \!= {0})|(?:\w+ \!= {0})'.format(constantToReplace)
eqPattern = '(?:(?:\w+(?:\(\))?)(?:\.\w+(?:\(\))?)+ == {0})|(?:\w+ == {0})'.format(constantToReplace)
nePatternSwapped = '(?:{0} \!= (?:\w+(?:\(\))?)(?:\.\w+(?:\(\))?)+)|(?:{0} \!= \w+)'.format(constantToReplace)
eqPatternSwapped = '(?:{0} == (?:\w+(?:\(\))?)(?:\.\w+(?:\(\))?)+)|(?:{0} == \w+)'.format(constantToReplace)

def findAndReplaceMatch(pattern, contents, replacedFunc, swapped):
    for match in re.findall(pattern, contents):
        firstSpace = match.index(' ')
        firstParam = match[:firstSpace]
        secondParam = match[match.index(' ',firstSpace+1)+1:]
        if swapped:
            replacement = replacedFunc.format(firstParam, secondParam)
        else:
            replacement = replacedFunc.format(secondParam, firstParam)
        print('"{0}" -> "{1}"'.format(match, replacement))
        contents = contents.replace(match, replacement)
    
    return contents


paths = Path('.').glob('**/*.java')
for path in paths:
    # because path is object not string
    path_in_str = str(path)
    # Do thing with the path
    
    f = open(path_in_str, 'r')
    contents = f.read()
    f.close()
    oldcontents = contents
    contents = findAndReplaceMatch(nePattern, contents, '!{}.equals({})', False)
    contents = findAndReplaceMatch(eqPattern, contents, '{}.equals({})', False)
    contents = findAndReplaceMatch(nePatternSwapped, contents, '!{}.equals({})', True)
    contents = findAndReplaceMatch(eqPatternSwapped, contents, '{}.equals({})', True)
    
    if contents == oldcontents:
        continue
    
    f = open(path_in_str, 'w')
    f.write(contents)
    f.close()
