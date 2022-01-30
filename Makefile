# Makefile para proyecto jlox
run:
	javac -d ./build ./lox/*.java
	java -cp ./build/ lox.Lox $(filter-out $@, $(MAKECMDGOALS))
	
generateast:
	javac -d ./build ./tool/GenerateAst.java
	java -cp ./build/ tool.GenerateAst ./lox/
	../astyle --style=java --keep-one-line-blocks --keep-one-line-statements -n ./lox/Expr.java

format:
	../astyle --style=java --keep-one-line-blocks --keep-one-line-statements -n ./**/*.java
	
fmt:format