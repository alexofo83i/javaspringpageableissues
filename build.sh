#export JAVA_HOME=/c/development/java/jdk-21
export JAVA_HOME=/c/Program\ Files/Eclipse\ Adoptium/jdk-21.0.5.11-hotspot
export PATH=$JAVA_HOME/bin:$PATH
#mvn -e -Dmaven.test.skip clean package install
mvn -e clean install

