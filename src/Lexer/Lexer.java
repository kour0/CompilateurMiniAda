package Lexer;

import Exceptions.Lexical.InvalidToken;
import Lexer.Tokens.Tag;
import Lexer.Tokens.Token;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Lexer class to read a file and return tokens
 *
 * @author Noé Steiner
 * @author Alexis Marcel
 * @author Lucas Laurent
 */
public class Lexer {

    /**
     * Lexer attributes
     */
    private final PeekingReader reader;
    private final Map<Pattern, Tag> keywords;
    private final Map<Pattern, Tag> ruledTerminals;
    private final Map<Pattern, Tag> operators;
    private int currentLine;
    private int currentChar;

    /**
     * Lexer constructor
     *
     * @param file the file to read
     * @throws IOException if the file cannot be read
     */
    public Lexer(File file) throws IOException {

        this.reader = new PeekingReader(new FileReader(file));
        this.currentLine = 1;
        this.currentChar = this.reader.read();
        this.keywords = Map.ofEntries(
                Map.entry(Pattern.compile("procedure"), Tag.PROCEDURE),
                Map.entry(Pattern.compile("is"), Tag.IS),
                Map.entry(Pattern.compile("begin"), Tag.BEGIN),
                Map.entry(Pattern.compile("end"), Tag.END),
                Map.entry(Pattern.compile(";"), Tag.SEMICOLON),
                Map.entry(Pattern.compile("type"), Tag.TYPE),
                Map.entry(Pattern.compile("access"), Tag.ACCESS),
                Map.entry(Pattern.compile("record"), Tag.RECORD),
                Map.entry(Pattern.compile(":"), Tag.COLON),
                Map.entry(Pattern.compile("function"), Tag.FUNCTION),
                Map.entry(Pattern.compile("return"), Tag.RETURN),
                Map.entry(Pattern.compile("in"), Tag.IN),
                Map.entry(Pattern.compile("out"), Tag.OUT),
                Map.entry(Pattern.compile("if"), Tag.IF),
                Map.entry(Pattern.compile("then"), Tag.THEN),
                Map.entry(Pattern.compile("elsif"), Tag.ELSIF),
                Map.entry(Pattern.compile("else"), Tag.ELSE),
                Map.entry(Pattern.compile("loop"), Tag.LOOP),
                Map.entry(Pattern.compile("for"), Tag.FOR),
                Map.entry(Pattern.compile("reverse"), Tag.REVERSE),
                Map.entry(Pattern.compile("while"), Tag.WHILE),
                Map.entry(Pattern.compile("rem"), Tag.REM),
                Map.entry(Pattern.compile("and"), Tag.AND),
                Map.entry(Pattern.compile("or"), Tag.OR),
                Map.entry(Pattern.compile("\\."), Tag.DOT),
                Map.entry(Pattern.compile("val"), Tag.VAL),
                Map.entry(Pattern.compile("\\("), Tag.OPEN_PAREN),
                Map.entry(Pattern.compile("\\)"), Tag.CLOSE_PAREN),
                Map.entry(Pattern.compile("true"), Tag.TRUE),
                Map.entry(Pattern.compile("false"), Tag.FALSE),
                Map.entry(Pattern.compile(","), Tag.COMMA)
        );

        this.operators = Map.ofEntries(
                Map.entry(Pattern.compile("\\+"), Tag.PLUS),
                Map.entry(Pattern.compile("-"), Tag.MINUS),
                Map.entry(Pattern.compile("\\*"), Tag.MULTI),
                Map.entry(Pattern.compile("/"), Tag.DIV),
                Map.entry(Pattern.compile("="), Tag.EQ),
                Map.entry(Pattern.compile("/="), Tag.NE),
                Map.entry(Pattern.compile("<"), Tag.LT),
                Map.entry(Pattern.compile("<="), Tag.LE),
                Map.entry(Pattern.compile(">"), Tag.GT),
                Map.entry(Pattern.compile(">="), Tag.GE),
                Map.entry(Pattern.compile(":="), Tag.ASSIGN)
        );

        this.ruledTerminals = Map.of(
                Pattern.compile("[A-Za-z][A-Za-z0-9_]*"), Tag.IDENT,
                Pattern.compile("[0-9]+"), Tag.ENTIER,
                Pattern.compile("'[A-Za-z]'"), Tag.CARACTERE
        );
    }

    /**
     * Get the next token from the file
     *
     * @return the next token
     * @throws IOException  if the file cannot be read
     * @throws InvalidToken if the lexer finds an invalid token
     */
    public Token nextToken() throws IOException, InvalidToken {
        StringBuilder lexeme = new StringBuilder();

        while (this.currentChar != -1) {

            if (this.isComment()) {
                this.skipComment();
            } else if (Character.isWhitespace((char) currentChar)) {
                this.skipWhitespace();
            } else {
                lexeme.append((char) currentChar);

                if (this.isEndOfToken()) {
                    currentChar = this.reader.read();
                    return this.matchToken(lexeme.toString());
                }
                currentChar = this.reader.read();
            }

        }

        this.reader.close();
        return new Token(Tag.EOF, this.currentLine, lexeme.toString());
    }

    /**
     * Check if the current character is the beginning of a comment
     *
     * @return true if the current character is the beginning of a comment, false otherwise
     * @throws IOException if the file cannot be read
     */
    private boolean isComment() throws IOException {
        return this.currentChar == '-' && this.reader.peek(1) == '-' && this.reader.peek(2) != '-';
    }

    /**
     * Skip the current comment
     *
     * @throws IOException if the file cannot be read
     */
    private void skipComment() throws IOException {
        while (this.currentChar != '\n' && this.currentChar != -1) {
            this.currentChar = this.reader.read();
        }
        if (this.currentChar != -1) {
            this.currentChar = this.reader.read();
        }
        this.currentLine++;
    }

    /**
     * Check if the current character is the end of a token
     *
     * @return true if the current character is the end of a token, false otherwise
     * @throws IOException if the file cannot be read
     */
    private boolean isEndOfToken() throws IOException {
        char current = (char) currentChar;
        char next = (char) this.reader.peek(1);

        boolean isCurrentLetterOrDigit = Character.isLetterOrDigit(current);
        boolean isNextLetterOrDigit = Character.isLetterOrDigit(next);
        boolean isNextWhitespace = Character.isWhitespace(next);

        return (isCurrentLetterOrDigit && !isNextLetterOrDigit) || (!isCurrentLetterOrDigit && (isNextLetterOrDigit || isNextWhitespace));
    }

    /**
     * Skip all whitespaces
     *
     * @throws IOException if the file cannot be read
     */
    private void skipWhitespace() throws IOException {
        while (Character.isWhitespace((char) this.currentChar)) {
            if (currentChar == '\n') {
                this.currentLine++;
            }
            currentChar = this.reader.read();
        }
    }

    /**
     * Match the lexeme with a pattern and return the corresponding token
     *
     * @param lexeme the lexeme to match
     * @return the corresponding token
     * @throws InvalidToken if the lexeme does not match any pattern
     */
    private Token matchToken(String lexeme) throws InvalidToken {
        List<Map<Pattern, Tag>> patterns = List.of(this.keywords, this.ruledTerminals, this.operators);

        for (Map<Pattern, Tag> pattern : patterns) {
            for (Map.Entry<Pattern, Tag> entry : pattern.entrySet()) {
                Pattern p = entry.getKey();
                Tag tag = entry.getValue();

                if (p.matcher(lexeme).matches()) {
                    return new Token(tag, this.currentLine, lexeme);
                }
            }
        }

        throw new InvalidToken(new Token(Tag.UNKNOWN, this.currentLine, lexeme));
    }

    /**
     * Display all tokens from the file in the standard output
     *
     * @throws IOException  if the file cannot be read
     * @throws InvalidToken if the lexer finds an invalid token
     */
    public void displayAllTokens() throws IOException, InvalidToken {
        Token token;
        List<Token> tokens = new ArrayList<>();
        while ((token = this.nextToken()).tag() != Tag.EOF) {
            tokens.add(token);
        }
        tokens.add(token);
        tokens.forEach(System.out::print);
    }

}
