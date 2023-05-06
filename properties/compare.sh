#!/bin/bash

for scenario in */; do
  cd $scenario
  for csvFile in *.csv; do
    [ -f "$csvFile" ] || break
    for resultFolder in */; do
      if [ -f "$resultFolder/$csvFile" ]; then
        echo "$resultFolder/$csvFile exists."
        cmp --silent $csvFile $resultFolder/$csvFile && echo '### SUCCESS: Files Are Identical! ###' || echo '### WARNING: Files Are Different! ###'
      else
        echo "$resultFolder/$csvFile does not exist."
      fi
    done
  done
  cd ..
done


