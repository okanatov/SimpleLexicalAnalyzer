package org.okanatov.lexer;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

public class ChainedLexerTest {
    private Token token;

    @Test
    public void testV() {
        ChainedLexer chainedLexer = new ChainedLexer(
                new ByteArrayInputStream("IVVVX".getBytes()),
                "V+");

        token = chainedLexer.readToken();
        assertEquals("I", token.toString());

        token = chainedLexer.readToken();
        assertEquals("VVV", token.toString());

        token = chainedLexer.readToken();
        assertEquals("X", token.toString());

        token = chainedLexer.readToken();
        assertEquals(null, token);
    }

    @Test
    public void testBraces() throws Exception {
        ChainedLexer chainedLexer = new ChainedLexer(
                new ChainedLexer(
                        new ByteArrayInputStream("a<b>c".getBytes()),
                        "<"),
                ">");

        token = chainedLexer.readToken();
        assertEquals("a", token.toString());

        token = chainedLexer.readToken();
        assertEquals("<", token.toString());

        token = chainedLexer.readToken();
        assertEquals("b", token.toString());

        token = chainedLexer.readToken();
        assertEquals(">", token.toString());

        token = chainedLexer.readToken();
        assertEquals("c", token.toString());

        token = chainedLexer.readToken();
        assertEquals(null, token);

    }
    @Test
    public void testLookAheadAndBehind() throws Exception {
        ChainedLexer chainedLexer = new ChainedLexer(new ByteArrayInputStream("IIVVXX".getBytes()), "(?<=I)V+(?=X)");

        token = chainedLexer.readToken();
        assertEquals("II", token.toString());

        token = chainedLexer.readToken();
        assertEquals("VV", token.toString());

        token = chainedLexer.readToken();
        assertEquals("XX", token.toString());

        token = chainedLexer.readToken();
        assertEquals(null, token);
    }



    @Test
    public void testIteration() {
        ChainedLexer chainedLexer =
                new ChainedLexer(
                        new ChainedLexer(
                                new ChainedLexer(
                                        new ByteArrayInputStream("IIIVX".getBytes()), "IV"),
                                "II"),
                        "X");

        for (Token aLexer : chainedLexer) {
            token = aLexer;
            System.out.println(token.toString());
        }
    }


    @Test
    public void test4() {
        ChainedLexer chainedLexer = new ChainedLexer(new ByteArrayInputStream("IVX".getBytes()), "IV");

        token = chainedLexer.readToken();
        assertEquals("IV", token.toString());

        token = chainedLexer.readToken();
        assertEquals("X", token.toString());

        token = chainedLexer.readToken();
        assertEquals(null, token);
    }

    @Test
    public void testComplex() {
        ChainedLexer chainedLexer =
                new ChainedLexer(
                        new ChainedLexer(
                                new ChainedLexer(
                                        new ByteArrayInputStream("IIIVX".getBytes()), "IV"),
                                "I{2}"),
                        "X");

        token = chainedLexer.readToken();
        assertEquals("II", token.toString());

        token = chainedLexer.readToken();
        assertEquals("IV", token.toString());

        token = chainedLexer.readToken();
        assertEquals("X", token.toString());

        token = chainedLexer.readToken();
        assertEquals(null, token);
    }
}
