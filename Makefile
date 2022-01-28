# Makefile para proyecto jlox
run:
	javac -d ./build ./lox/*.java
	java -cp ./build/ lox.Lox $(filter-out $@, $(MAKECMDGOALS))
	


format:
	../astyle --style=java --keep-one-line-blocks --keep-one-line-statements -n ./lox/*.java

	
fmt:format