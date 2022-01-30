
/*
Implementa la gramática para las expresiones de Lox definidas.
expression  -> literal | unary | binary | grouping;
literal     -> NUMBER | STRING | "true" | "false" | "nil";
grouping    -> "(" expression ")";
unary       -> ( "-" | "!" ) expression;
binary      -> expression operator expression;
operator    -> "==" | "!=" | "<" | "<=" | ">" | "=>" | "+" | "-" | "*" | "/";

Genera el archivo Expr.java para el paquete lox
*/

package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) {
        if(args.length!=1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }

        String outputDir=args[0];
        try {
            defineAst(outputDir, "Expr", Arrays.asList(
                          "Binary   : Expr left, Token operator, Expr right",
                          "Grouping : Expr expression",
                          "Literal  : Object value",
                          "Unary    : Token operator, Expr right"
                      ));
        } catch (Exception e) {
            System.err.println("GenerateAst Exception: "+e);
        }

    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {

        String path =outputDir+"/"+baseName+".java";
        PrintWriter writer=new PrintWriter(path, "UTF-8");

        writer.println("package lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class "+baseName+" {");

        // The AST classes
        for (String type:types) {
            String className=type.split(":")[0].trim();
            String fields=type.split(":")[1].trim();

            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }
    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("\tstatic class "+className+ " extends "+ baseName+" {");

        //contructor
        writer.println("\t\t"+className+"("+fieldList+") {");

        // store parameters in fields
        String[] fields=fieldList.split(",");
        for(String field:fields) {
            String name=field.trim().split(" ")[1].trim();
            writer.println("\t\t\tthis."+name+"="+name+";");
        }
        writer.println("\t\t}");

        //fields
        writer.println();
        for(String field:fields) {
            writer.println("\t\tfinal "+field.trim()+";");
        }
        writer.println("\t}");
    }
}
