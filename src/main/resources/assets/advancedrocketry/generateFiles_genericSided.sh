#!/bin/bash

echo '{' > blockstates/$1.json
echo '    "variants": {' >> blockstates/$1.json
echo '        "normal": { "model": "advancedrocketry':$1'" },' >> blockstates/$1.json
echo '    }' >> blockstates/$1.json
echo '}' >> blockstates/$1.json

echo '{' > models/block/$1.json
echo '    "parent": "block/cube_all",'>> models/block/$1.json
echo '    "textures": {' >> models/block/$1.json
echo '        "all": "advancedrocketry:blocks/'$2'",'>> models/block/$1.json
echo '    }'>> models/block/$1.json
echo '}'>> models/block/$1.json

echo '{' > models/item/$1.json
echo '    "parent": "advancedrocketry:block'/$1'"' >> models/item/$1.json
echo '}' >> models/item/$1.json
