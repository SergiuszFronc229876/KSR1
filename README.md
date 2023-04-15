# Klasyfikacja dokumentów tekstowych
Projektem jest aplikacja, wykorzystująca klasyfikator k-NN do klasyfikacji tekstów.
Celem projektu jest badanie wpływu poszczególnych cech na jakość klasyfikacji, poprzez analizę [artykułów](https://archive.ics.uci.edu/ml/datasets/Reuters-21578+Text+Categorization+Collection) pod kątem dziesięciu cech i ich wpływu na wynik końcowy.
Artykuły, wykorzystane w projekcie, pochodzą z kategorii "places" i mają dokładnie jedną etykietę ze zbioru: west-germany, usa, france, uk, canada, japan.

# Uruchamianie z pominięciem GUI poprzez przekazanie konfiguracji.
#### Przejdz do katalogu properties i uruchom odpowiedni dla twojego srodowiska skrypt dodając sciezkę do pliku z konfiguracją nie podajac rozszerzenia
#### Przykładowo:
```shell
cd properties
./runapp.sh k/env1
```
## Jest tez opcja uruchomienia wszystkich konfiguracji naraz poprzez:
```shell
cd properties
./runapp.sh all
```