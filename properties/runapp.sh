#!/bin/bash
function setJavaOpts() {
  source $1.properties
  JAVA_OPTS=""
  if [ -n "$PERCENTAGE_OF_THE_TRAINING_SET" ]; then
    JAVA_OPTS="$JAVA_OPTS -DPERCENTAGE_OF_THE_TRAINING_SET=$PERCENTAGE_OF_THE_TRAINING_SET"
  fi

  if [ -n "$NEIGHBORS" ]; then
    JAVA_OPTS="$JAVA_OPTS -DNEIGHBORS=$NEIGHBORS"
  fi

  if [ -n "$METRIC" ]; then
    JAVA_OPTS="$JAVA_OPTS -DMETRIC=$METRIC"
  fi

  if [ -n "$FEATURES" ]; then
    JAVA_OPTS="$JAVA_OPTS -DFEATURES=$FEATURES"
  fi

  if ! [ -n "$CSVDIR" ]; then
    #jesli nie jest ustawione to plik z wynikiem bedzie zapisany w tym samym folderze co plik konfiguracyjny
    CWD=$(pwd)
    CSVDIR="$CWD/$1.csv"
    JAVA_OPTS="$JAVA_OPTS -DCSVDIR="$CSVDIR""
  fi
  if ! [ -n "$GUIMODE" ]; then
    #jesli nie jest ustawione to plik z wynikiem bedzie zapisany w tym samym folderze co plik konfiguracyjny
    JAVA_OPTS="$JAVA_OPTS -DGUIMODE=false"
  fi
}

if ! [ -n "$1" ]; then
  echo "Uzyj:"
  echo "all -> aby uruchomic obliczenia dla wszystkich konfiguracji"
  echo "Jesli chcesz tylko dla jednej konfiguracji to podaj sciezkę do pliku z konfiguracją nie podajac rozszerzenia np. k/env1"
else
  if [ "$1" == "all" ]; then
    for confFile in $(find . -type f -name "*.properties" | sed 's/\.properties$//1'); do
      setJavaOpts "$confFile"
      cd ..
      java $JAVA_OPTS -jar target/KSR1-1.0-SNAPSHOT-jar-with-dependencies.jar
      CSVDIR=""
      cd properties
    done
  else
    setJavaOpts $1
    cd ..
    java $JAVA_OPTS -jar target/KSR1-1.0-SNAPSHOT-jar-with-dependencies.jar
    cd properties
  fi
fi
