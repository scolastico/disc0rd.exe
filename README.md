# disc0rd.exe
[![](https://github.com/scolastico/disc0rd.exe/workflows/Java%20CI/badge.svg)](https://github.com/scolastico/disc0rd.exe/actions)
[![](https://shields.scolasti.co/github/v/release/scolastico/disc0rd.exe?include_prereleases)](https://github.com/scolastico/disc0rd.exe/releases)
[![](https://shields.scolasti.co/github/repo-size/scolastico/disc0rd.exe)](https://github.com/scolastico/disc0rd.exe/archive/master.zip)
[![](https://shields.scolasti.co/github/languages/top/scolastico/disc0rd.exe)](#)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/71d533724e734db8b21952959423500d)](#)

## Installation

To compile the latest version execute following:

```bash
git clone git@github.com:scolastico/disc0rd.exe.git && cd ./disc0rd.exe/ && mvn clean install compile assembly:single --file pom.xml && cp ./target/disc0rd-exe-jar-with-dependencies.jar ../disc0rd-exe-jar-with-dependencies.jar && cd ../ && rm -Rf ./disc0rd.exe/
```
