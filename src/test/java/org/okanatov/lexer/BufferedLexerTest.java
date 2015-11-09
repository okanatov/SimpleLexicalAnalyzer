package org.okanatov.lexer;

import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class BufferedLexerTest {
    @Test
    public void testSimple() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("123IVX456"), "\\D", 3);
        assertEquals("123", lexer.readChar());
        assertEquals("I", lexer.readChar());
        assertEquals("V", lexer.readChar());
        assertEquals("X", lexer.readChar());
        assertEquals("456", lexer.readChar());
    }

    @Test
    public void testSimple2() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("123IVX456"), "\\D+", 4);
        assertEquals("123", lexer.readChar());
        assertEquals("IVX", lexer.readChar());
        assertEquals("456", lexer.readChar());
    }

    @Test
    public void testSimple3() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("123IVX456"), "\\d", 6);
        assertEquals("1", lexer.readChar());
        assertEquals("2", lexer.readChar());
        assertEquals("3", lexer.readChar());
        assertEquals("IVX", lexer.readChar());
        assertEquals("4", lexer.readChar());
        assertEquals("5", lexer.readChar());
        assertEquals("6", lexer.readChar());
    }

    @Test
    public void testSimple4() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("123IVX456"), "\\d+", 6);
        assertEquals("123", lexer.readChar());
        assertEquals("IVX", lexer.readChar());
        assertEquals("456", lexer.readChar());
    }

    @Test
    public void testSimple5() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("123IVX456"), "(?<=\\d)\\D+(?=\\d)", 4);
        assertEquals("123", lexer.readChar());
        assertEquals("IVX", lexer.readChar());
        assertEquals("456", lexer.readChar());
    }

    @Test
    public void testSimple6() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("12 3I VX456"), "\\D+", 6);
        assertEquals("12", lexer.readChar());
        assertEquals(" ", lexer.readChar());
        assertEquals("3", lexer.readChar());
        assertEquals("I VX", lexer.readChar());
        assertEquals("456", lexer.readChar());
    }

    @Test
    public void testSimple7() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("123IVX456"), "\\d+\\D+\\d", 4);
        assertEquals("123IVX4", lexer.readChar());
        assertEquals("56", lexer.readChar());
    }

    @Test
    public void testSimple8() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("IVX"), "V", 2);
        assertEquals("I", lexer.readChar());
        assertEquals("V", lexer.readChar());
        assertEquals("X", lexer.readChar());
    }

    @Test
    public void testSimple9() throws Exception {
        BufferedLexer lexer = new BufferedLexer(new StringReader("123IVX456"), "(?<=23)\\D+(?=45)", 4);
        assertEquals("123", lexer.readChar());
        assertEquals("IVX", lexer.readChar());
        assertEquals("456", lexer.readChar());
    }
}
