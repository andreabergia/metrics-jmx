package com.andreabergia.metricsjmx;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry metrics = new MetricRegistry();
        registerMemoryMetrics(metrics);
        initReporters(metrics);
        waitUntilKilled();
    }

    private static void registerMemoryMetrics(MetricRegistry metrics) {
        Gauge<Long> getFreeMemory = () -> toMb(Runtime.getRuntime().freeMemory());
        Gauge<Long> getTotalMemory = () -> toMb(Runtime.getRuntime().totalMemory());
        metrics.register(MetricRegistry.name(Main.class, "memory.free.mb"), getFreeMemory);
        metrics.register(MetricRegistry.name(Main.class, "memory.total.mb"), getTotalMemory);
    }

    private static long toMb(long bytes) {
        return bytes / 1024 / 1024;
    }

    private static void initReporters(MetricRegistry metrics) {
        initConsoleReporter(metrics);
        initJmxReporter(metrics);
    }

    private static void initJmxReporter(MetricRegistry metrics) {
        final JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
        reporter.start();
    }

    private static void initConsoleReporter(MetricRegistry metrics) {
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }

    private static void waitUntilKilled() throws InterruptedException {
        List<String> memoryWaste = new ArrayList<>();
        char[] data = new char[1_000_000];
        while (true) {
            memoryWaste.add(String.copyValueOf(data));
            Thread.currentThread().sleep(100);
        }
    }
}
