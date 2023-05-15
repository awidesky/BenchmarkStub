/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.awidesky;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/*
 * mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=com.awidesky -DartifactId=BenchmarkStub -Dversion=1.0
 *
 * 
 * Step 1: build Maven project
 *  1. Right click on project,
 *  2. Select Run As,
 *  3. Select Maven Build and specify goals as clean install
 * 
 * Step 2: run tests
 * 1. Right click on project,
 * 2. Select Run As,
 * 3. Select Java Application and choose either Main - org.openjdk.jmh or the main you created
 * 
 * */

@Warmup(iterations = 2) // Warm-up Iteration
@Measurement(iterations = 4)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1)
//hongsungmin made
public class MyBenchmark {

	private static Path from;
	private static Path to;
	
	private static final int FROMFILESIZE = 1024 * 1024 * 1024;
	private static final int BUFFERSIZE = 32 * 1024;

    @Setup(Level.Trial)
    public void setup() throws IOException {
    	System.out.println("\n\tmaking dummy file...");
    	from = Files.createTempFile("from", ".bin");
    	to = Files.createTempFile("to", ".bin");
    	//from = Paths.get("C:\\Users\\CKIRUser\\Documents\\aa", "from.bin");
    	//to = Paths.get("C:\\Users\\CKIRUser\\Documents\\aa", "to.bin");
    	
    	from.toFile().deleteOnExit();
    	to.toFile().deleteOnExit();
    	
    	Random r = new Random();
    	byte[] buf = new byte[64 * 1024];
    	int totalwrite = 0;
		try (FileOutputStream fo = new FileOutputStream(from.toFile());) {
			while(totalwrite < FROMFILESIZE) {
				r.nextBytes(buf);
				fo.write(buf);
				totalwrite += buf.length;
			}
			
		}
		System.out.println("\tdummy file size : " + formatFileSize(Files.size(from)));
    }
    
    @Setup(Level.Iteration)
    public void makeTo() throws IOException {
		Files.deleteIfExists(to);
		if(Files.notExists(to)) Files.createFile(to);
    }

    @Benchmark
	public void streamAsStream(Blackhole bh) throws IOException {
		try (FileInputStream fi = new FileInputStream(from.toFile());
				FileOutputStream fo = new FileOutputStream(to.toFile());) {
			byte[] buf = new byte[BUFFERSIZE];

			while (streamWrite(fo, buf, streamRead(fi, buf)));
		}
		assert(Files.size(from) == Files.size(to));
	}
    
    @Benchmark
    public void streamAsChannel(Blackhole bh) throws IOException {
    	try (FileInputStream i = new FileInputStream(from.toFile());
				FileOutputStream o = new FileOutputStream(to.toFile());
    			FileChannel fi = i.getChannel();
				FileChannel fo = o.getChannel();) {
			ByteBuffer buf = ByteBuffer.allocate(BUFFERSIZE);

			do {
				channelRead(fi, buf);
			} while (channelWrite(fo, buf));
		}
    	assert(Files.size(from) == Files.size(to));
    }
    
    @Benchmark
    public void channelAsStream(Blackhole bh) throws IOException {
    	try (InputStream fi = Channels.newInputStream(FileChannel.open(from, StandardOpenOption.READ));
    			OutputStream fo = Channels.newOutputStream(FileChannel.open(to, StandardOpenOption.WRITE));) {
    		byte[] buf = new byte[BUFFERSIZE];

			while (streamWrite(fo, buf, streamRead(fi, buf)));
		}
    	assert(Files.size(from) == Files.size(to));
    }
    
    @Benchmark
    public void channelAsChannel(Blackhole bh) throws IOException {
    	try (FileChannel fi = FileChannel.open(from, StandardOpenOption.READ);
				FileChannel fo = FileChannel.open(to, StandardOpenOption.WRITE);) {
			ByteBuffer buf = ByteBuffer.allocate(BUFFERSIZE);

			do {
				channelRead(fi, buf);
			} while (channelWrite(fo, buf));
		}
    	assert(Files.size(from) == Files.size(to));
    }
    

    private static boolean streamWrite(OutputStream fo, byte[] buffer, int len) throws IOException {
    	if(len == -1) return false;
    	fo.write(buffer, 0, len);
    	//System.out.println("write : " + len);
    	return true;
    }
    private static boolean channelWrite(WritableByteChannel ch, ByteBuffer buffer) throws IOException {
    	if(!buffer.hasRemaining()) return false;
    	while(buffer.hasRemaining()) ch.write(buffer);
    	buffer.clear();
    	return true;
    }
    private static int streamRead(InputStream fi, byte[] buffer) throws IOException {
    	int totalread = 0;
    	int nowread = 0;
    	do {
    		nowread = fi.read(buffer, totalread, buffer.length - totalread);
    		if(nowread == -1) {
    			if(totalread == 0) totalread = -1;
    			break;
    		}
    		totalread += nowread;
    	} while (totalread != buffer.length);
    	//System.out.println("totalread : " + totalread);
    	return totalread;
    }
    private static void channelRead(ReadableByteChannel ch, ByteBuffer buffer) throws IOException {
    	while(buffer.hasRemaining() && ch.read(buffer) != -1);
    	buffer.flip();
    }
    
	private static String formatFileSize(long fileSize) {
		
		if(fileSize == 0L) return "0.00byte";
		
		switch ((int)(Math.log(fileSize) / Math.log(1024))) {
		
		case 0:
			return String.format("%d", fileSize) + "byte";
		case 1:
			return String.format("%.2f", fileSize / 1024.0) + "KB";
		case 2:
			return String.format("%.2f", fileSize / (1024.0 * 1024)) + "MB";
		case 3:
			return String.format("%.2f", fileSize / (1024.0 * 1024 * 1024)) + "GB";
		}
		return String.format("%.2f", fileSize / (1024.0 * 1024 * 1024 * 1024)) + "TB";
		
	}
	
}


/*
Benchmark                     Mode  Cnt      Score       Error  Units
MyBenchmark.channelAsChannel  avgt    4  10642.582 ± 11668.644  ms/op
MyBenchmark.channelAsStream   avgt    4  10308.973 ± 10001.679  ms/op
MyBenchmark.streamAsChannel   avgt    4  11097.935 ±  7846.946  ms/op
MyBenchmark.streamAsStream    avgt    4   9437.627 ±  4752.515  ms/op
 
 
Benchmark                     Mode  Cnt      Score       Error  Units
MyBenchmark.channelAsChannel  avgt    4   8878.731 ± 12772.751  ms/op
MyBenchmark.channelAsStream   avgt    4  10026.648 ±  6410.937  ms/op
MyBenchmark.streamAsChannel   avgt    4   9055.661 ±  4374.498  ms/op
MyBenchmark.streamAsStream    avgt    4   8914.932 ±  7929.816  ms/op
 */