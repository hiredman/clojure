/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package clojure.lang;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.lang.*;

public class LispReaderII {
static private int readUnicodeChar(PushbackReader r, int initch, int base, int length, boolean exact) throws Exception {
	int uc = Character.digit(initch, base);
	if(uc == -1)
		throw new IllegalArgumentException("Invalid digit: " + initch);
	int i = 1;
	for(; i < length; ++i)
		{
		int ch = r.read();
		if(ch == -1 || LispReader.isWhitespace(ch) || LispReader.isMacro(ch))
			{
            r.unread(ch);
			break;
			}
		int d = Character.digit(ch, base);
		if(d == -1)
			throw new IllegalArgumentException("Invalid digit: " + (char) ch);
		uc = uc * base + d;
		}
	if(i != length && exact)
		throw new IllegalArgumentException("Invalid character length: " + i + ", should be: " + length);
	return uc;
}

public static class StringReader extends AFn{
	public Object invoke(Object reader, Object doublequote) throws Exception{
		StringBuilder sb = new StringBuilder();
		Reader r = (Reader) reader;

		for(int ch = r.read(); ch != '"'; ch = r.read())
			{
			if(ch == -1)
				throw new Exception("EOF while reading string");
			if(ch == '\\')	//escape
				{
				ch = r.read();
				if(ch == -1)
					throw new Exception("EOF while reading string");
				switch(ch)
					{
					case 't':
						ch = '\t';
						break;
					case 'r':
						ch = '\r';
						break;
					case 'n':
						ch = '\n';
						break;
					case '\\':
						break;
					case '"':
						break;
					case 'b':
						ch = '\b';
						break;
					case 'f':
						ch = '\f';
						break;
					case 'u':
					{
					ch = r.read();
					if (Character.digit(ch, 16) == -1)
					    throw new Exception("Invalid unicode escape: \\u" + (char) ch);
					ch = readUnicodeChar((PushbackReader) r, ch, 16, 4, true);
					break;
					}
					default:
					{
					if(Character.isDigit(ch))
						{
						ch = readUnicodeChar((PushbackReader) r, ch, 8, 3, false);
						if(ch > 0377)
							throw new Exception("Octal escape sequence must be in range [0, 377].");
						}
					else
						throw new Exception("Unsupported escape character: \\" + (char) ch);
					}
					}
				}
			sb.append((char) ch);
			}
		return sb.toString();
	}
}

public static class CommentReader extends AFn{
	public Object invoke(Object reader, Object semicolon) throws Exception{
		Reader r = (Reader) reader;
		int ch;
		do
			{
			ch = r.read();
			} while(ch != -1 && ch != '\n' && ch != '\r');
		return r;
	}

}

public static class DiscardReader extends AFn{
	public Object invoke(Object reader, Object underscore) throws Exception{
		PushbackReader r = (PushbackReader) reader;
		LispReader.read(r, true, null, true);
		return r;
	}
}

public static class WrappingReader extends AFn{
	final Symbol sym;

	public WrappingReader(Symbol sym){
		this.sym = sym;
	}

	public Object invoke(Object reader, Object quote) throws Exception{
		PushbackReader r = (PushbackReader) reader;
		Object o = LispReader.read(r, true, null, true);
		return RT.list(sym, o);
	}

}
public static class ReadToken extends AFn {
	public Object invoke(Object readerO, Object initchO) throws Exception{
      PushbackReader r = (PushbackReader) readerO;
      char initch = (Character)initchO;
      StringBuilder sb = new StringBuilder();
	  sb.append(initch);
	  for(; ;)
		{
		int ch = r.read();
		if(ch == -1 || LispReader.isWhitespace(ch) || LispReader.isTerminatingMacro(ch))
			{
            r.unread(ch);
			return sb.toString();
			}
		sb.append((char) ch);
		}
	}
}
public static class LispReader1 extends AFn {
  public Object invoke (Object rO, Object eofIsErrorO, Object eofValue, Object isRecursiveO) throws Exception {
    PushbackReader r = (PushbackReader) rO;
    boolean eofIsError = (Boolean) eofIsErrorO;
    Boolean isRecursive = (Boolean) isRecursiveO;
	try
		{
		for(; ;)
			{
			int ch = r.read();

			while(LispReader.isWhitespace(ch))
				ch = r.read();

			if(ch == -1)
				{
				if(eofIsError)
					throw new Exception("EOF while reading");
				return eofValue;
				}

			if(Character.isDigit(ch))
				{
				Object n = LispReader.readNumber(r, (char) ch);
				if(RT.suppressRead())
					return null;
				return n;
				}

			IFn macroFn = LispReader.getMacro(ch);
			if(macroFn != null)
				{
				Object ret = macroFn.invoke(r, (char) ch);
				if(RT.suppressRead())
					return null;
				//no op macros return the reader
				if(ret == r)
					continue;
				return ret;
				}

			if(ch == '+' || ch == '-')
				{
				int ch2 = r.read();
				if(Character.isDigit(ch2))
					{
					r.unread(ch2);
					Object n = LispReader.readNumber(r, (char) ch);
					if(RT.suppressRead())
						return null;
					return n;
					}
				r.unread(ch2);
				}

			String token = LispReader.readToken(r, (char) ch);
			if(RT.suppressRead())
				return null;
			return LispReader.interpretToken(token);
			}
		}
	catch(Exception e)
		{
		if(isRecursive || !(r instanceof LineNumberingPushbackReader))
			throw e;
		LineNumberingPushbackReader rdr = (LineNumberingPushbackReader) r;
		//throw new Exception(String.format("ReaderError:(%d,1) %s", rdr.getLineNumber(), e.getMessage()), e);
		throw new LispReader.ReaderException(rdr.getLineNumber(), e);
		}
}}

public static class RegexReader extends AFn {
	static IFn stringrdr = new LispReader.StringReader1();

	public Object invoke(Object reader, Object doublequote) throws Exception{
		StringBuilder sb = new StringBuilder();
		Reader r = (Reader) reader;
		for(int ch = r.read(); ch != '"'; ch = r.read())
			{
			if(ch == -1)
				throw new Exception("EOF while reading regex");
			sb.append( (char) ch );
			if(ch == '\\')	//escape
				{
				ch = r.read();
				if(ch == -1)
					throw new Exception("EOF while reading regex");
				sb.append( (char) ch ) ;
				}
			}
		return Pattern.compile(sb.toString());
	}
}


}

