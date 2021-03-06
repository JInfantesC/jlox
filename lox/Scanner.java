package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens= new ArrayList<>();

    private static final Map<String, TokenType> keywords;

    static {
        keywords=new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }


    // To keep track where the scanner is
    private int start=0;
    private int current=0;
    private int line=1;


    Scanner(String source) {
        this.source=source;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start=current;// We are at the beginning of the next lexeme.
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c=advance();
        switch(c) {
        case '(': addToken(LEFT_PAREN); break;
        case ')': addToken(RIGHT_PAREN); break;
        case '{': addToken(LEFT_BRACE); break;
        case '}': addToken(RIGHT_BRACE); break;
        case ',': addToken(COMMA); break;
        case '.': addToken(DOT); break;
        case '-': addToken(MINUS); break;
        case '+': addToken(PLUS); break;
        case ';': addToken(SEMICOLON); break;
        case '*': addToken(STAR); break;
        case '!':
            addToken(match('=')? BANG_EQUAL:BANG);
            break;
        case '=':
            addToken(match('=')? EQUAL_EQUAL:EQUAL);
            break;
        case '<':
            addToken(match('=')? LESS_EQUAL:LESS);
            break;
        case '>':
            addToken(match('=')? GREATER_EQUAL:GREATER);
            break;
        case '/':
            if(match('/')) {
                // A comment goes until the end of the line
                while(peek()!='\n' && !isAtEnd()) advance();
            } else if(match('*')) {
                // Block comment with /*. We need to ignore everything until */
                blockComment();
            } else {
                addToken(SLASH);
            }
            break;
        case ' ':
        case '\r':
        case '\t':
            break;
        case '\n':
            line++;
            break;
        case '"': string(); break;
        default:
            // Para n??meros, en default comprobamos si es digito para no hacer un case por cada n??mero.
            if(isDigit(c)) {
                number();
            } else if(isAlpha(c)) { // Para palabras reservadas e identificadores
                identifier();
            } else {
                Lox.error(line, "Unexpected character."); // Mezclar varios en un ??nico error ser??a mejor UX
            }
            break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text=source.substring(start, current);
        TokenType type=keywords.get(text);
        if(type==null) type=IDENTIFIER;

        addToken(type);
    }

    private void number() {
        while(isDigit(peek())) advance();

        // Look for a fractional part.
        if(peek()=='.' && isDigit(peekNext())) {
            // consume '.'
            advance();
        }

        while(isDigit(peek())) advance();

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while(peek()!='"' && !isAtEnd()) {
            if(peek()=='\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        advance(); // The closing ".

        // Trim the surrounding quotes.
        String value = source.substring(start+1, current-1);
        addToken(STRING, value);

    }

    private void blockComment() {
        boolean endBlock=false;
        while(!endBlock && !isAtEnd()) {
            if(peek()=='*' && peekNext()=='/') { // Si encontramos un *, miramos si el siguiente es / para cerrar el comentario.
                endBlock=true;
            } else if(peek()=='\n') { // Si hay un salto de linea, contabilizarlo.
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated block comment.");
            return;
        }
        advance(); // The closing /.
    }

    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if (source.charAt(current)!=expected) return false;

        current++;
        return true;
    }
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if(current+1>=source.length()) return '\0';
        return source.charAt(current+1);
    }

    private boolean isAlpha(char c) {
        return  (c>='a' && c<='z') ||
                (c>='A' && c<='Z') ||
                c=='_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c)|| isDigit(c);
    }

    private boolean isDigit(char c) {
        return c>='0' && c<='9';
    }

    private boolean isAtEnd() {
        return current>=source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // Object literal, para que acepte, strings, doubles, o lo que sea.
    private void addToken(TokenType type, Object literal) {
        String text= source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
