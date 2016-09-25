#!/bin/bash

echo '{' > blockstates/$1.json
echo '    "defaults": {' >> blockstates/$1.json
echo '        "textures": {'>> blockstates/$1.json
echo '            "#None": "advancedrocketry:/models/'$2'"'>> blockstates/$1.json
echo '        },'>> blockstates/$1.json
echo '        "model": "advancedrocketry:'$3'.obj"'>> blockstates/$1.json
echo '    },'>> blockstates/$1.json
echo '    "variants": {'>> blockstates/$1.json
echo '        "facing=north": { },'>> blockstates/$1.json
echo '        "facing=south": {  "y": 180 },'>> blockstates/$1.json
echo '        "facing=west":  {  "y": 270 },'>> blockstates/$1.json
echo '        "facing=east":  {  "y": 90 },'>> blockstates/$1.json
echo '        "facing=up":  { "x": 90 },'>> blockstates/$1.json
echo '        "facing=down":  { "x": -90 },'>> blockstates/$1.json
echo '        "normal": [{}],'>> blockstates/$1.json
echo '        "inventory": [{'>> blockstates/$1.json
echo '            "transform": {'>> blockstates/$1.json
echo '                    "translation": [ 0, 0, 0],'>> blockstates/$1.json
echo '                    "scale": 1.0'>> blockstates/$1.json
echo '                }'>> blockstates/$1.json
echo '        }]'>> blockstates/$1.json
echo '    }'>> blockstates/$1.json
echo '}'>> blockstates/$1.json
