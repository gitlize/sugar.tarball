package jp.ac.kobe_u.cs.sugar;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Logger {
	private static final long MEGA = 1000000; 

	public static void print(String message) {
		System.out.print(message);
	}

	public static void println(String message) {
		System.out.println(message);
	}

	public static void log(String message) {
		print("c ");
		println(message);
	}

	static void status() {
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapUsage = mbean.getHeapMemoryUsage();
//		long heapInit = heapUsage.getInit() / MEGA;
		long heapUsed = heapUsage.getUsed() / Logger.MEGA;
//		long heapCommitted = heapUsage.getCommitted() / MEGA;
		long heapMax = heapUsage.getMax() / Logger.MEGA;
		MemoryUsage nonHeapUsage = mbean.getNonHeapMemoryUsage();
//		long nonHeapInit = nonHeapUsage.getInit() / MEGA;
		long nonHeapUsed = nonHeapUsage.getUsed() / Logger.MEGA;
//		long nonHeapCommitted = nonHeapUsage.getCommitted() / MEGA;
		long nonHeapMax = nonHeapUsage.getMax() / Logger.MEGA;
		log(
				"Heap : " + heapUsed + " MB used (max " + heapMax + " MB), " +
				"NonHeap : " + nonHeapUsed + " MB used (max " + nonHeapMax + " MB)"
		);
	}

}
