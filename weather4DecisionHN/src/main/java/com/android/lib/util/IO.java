package com.android.lib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


public final class IO {
	
	public static final int BUFFER = 4 * 1024;
	
	public static void copy(File in, OutputStream out) throws FileNotFoundException, IOException {
		copy(IO.createInputStream(in), out);
	}
	
	public static void copy(File in, File out) throws FileNotFoundException, IOException {
		IO.copy(IO.createInputStream(in), out);
	}
	
	public static void copy(InputStream in, File out) throws FileNotFoundException, IOException {
		copy(in, new BufferedOutputStream(new FileOutputStream(out)));
	}
	
	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] bytes = new byte[BUFFER];
		for (int length = in.read(bytes); length > 0; length = in.read(bytes)) {
			out.write(bytes, 0, length);
		}
		in.close();
		out.close();
	}
	
	public static InputStream createInputStream(File file) throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	public static Reader createReader(File file) throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}
	
	public static Writer createWriter(File file) throws IOException {
		return new BufferedWriter(new FileWriter(file));
	}
	
	public static byte[] readAsBytes(File in) throws FileNotFoundException, IOException {
		java.io.ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(IO.createInputStream(in), out);
		return out.toByteArray();
	}
	
	public static byte[] readAsBytes(InputStream in) throws FileNotFoundException, IOException {
		java.io.ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}
	
	public static String readAsString(File in) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		return new String(IO.readAsBytes(in), "UTF8");
	}
	
	public static String readAsString(InputStream in) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		return new String(IO.readAsBytes(in), "UTF8");
	}
	
	public static void write(String content, File file) throws IOException {
		Writer writer = IO.createWriter(file);
		writer.write(content);
		writer.close();
	}
	
}
