# Makefile para proyecto jlox
run:
	javac -d ./build ./lox/*.java
	java -cp ./build/ lox.Lox $(filter-out $@, $(MAKECMDGOALS))
	
	
  