#!/bin/bash

echo '{' > blockstates/$1.json
echo '    "variants": {' >> blockstates/$1.json
echo '        "facing=north,state=false": { "model": "advancedrocketry':$1'" },' >> blockstates/$1.json
echo '        "facing=south,state=false": { "model": "advancedrocketry':$1'", "y": 180 },' >> blockstates/$1.json
echo '        "facing=west,state=false":  { "model": "advancedrocketry':$1'", "y": 270 },' >> blockstates/$1.json
echo '        "facing=east,state=false":  { "model": "advancedrocketry':$1'", "y": 90 },' >> blockstates/$1.json
echo '        "facing=north,state=true": { "model": "advancedrocketry':$1'_on" },' >> blockstates/$1.json
echo '        "facing=south,state=true": { "model": "advancedrocketry':$1'_on", "y": 180 },' >> blockstates/$1.json
echo '        "facing=west,state=true":  { "model": "advancedrocketry':$1'_on", "y": 270 },' >> blockstates/$1.json
echo '        "facing=east,state=true":  { "model": "advancedrocketry':$1'_on", "y": 90 },' >> blockstates/$1.json
echo '    }' >> blockstates/$1.json
echo '}' >> blockstates/$1.json

echo '{' > models/block/$1.json
echo '    "parent": "block/orientable",'>> models/block/$1.json
echo '    "textures": {' >> models/block/$1.json
echo '        "top": "libvulpes:blocks/machineGeneric",'>> models/block/$1.json
echo '        "front": "advancedrocketry:blocks/'$2'",'>> models/block/$1.json
echo '        "side": "libvulpes:blocks/machineGeneric"'>> models/block/$1.json
echo '    }'>> models/block/$1.json
echo '}'>> models/block/$1.json

echo '{' > models/block/$1_on.json
echo '    "parent": "block/orientable",'>> models/block/$1_on.json
echo '    "textures": {' >> models/block/$1_on.json
echo '        "top": "libvulpes:blocks/machineGeneric",'>> models/block/$1_on.json
echo '        "front": "advancedrocketry:blocks/'$3'",'>> models/block/$1_on.json
echo '        "side": "libvulpes:blocks/machineGeneric"'>> models/block/$1_on.json
echo '    }'>> models/block/$1_on.json
echo '}'>> models/block/$1_on.json

echo '{' > models/item/$1.json
echo '    "parent": "advancedrocketry:block'/$1'"' >> models/item/$1.json
echo '}' >> models/item/$1.json

echo '{' > models/item/$1_on.json
echo '    "parent": "advancedrocketry:block'/$1'_on"' >> models/item/$1_on.json
echo '}' >> models/item/$1_on.json
