package org.apache.storm.benchmark.tools;

import org.apache.commons.cli.*;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option runTimeOpt = OptionBuilder.hasArgs(1)
                .withArgName("ms")
                .withLongOpt("time")
                .withDescription("How long to run each benchmark in ms.")
                .create("t");
        options.addOption(runTimeOpt);

        Option pollTimeOpt = OptionBuilder.hasArgs(1)
                .withArgName("ms")
                .withLongOpt("poll")
                .withDescription("Metrics polling interval in ms.")
                .create("P");
        options.addOption(pollTimeOpt);

        Option reportPathOpt = OptionBuilder.hasArgs(1)
                .withArgName("path")
                .withLongOpt("path")
                .withDescription("Directory where reports will be saved.")
                .create("p");
        options.addOption(reportPathOpt);


        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.getArgs().length != 1) {
            usage(options);
            System.exit(1);
        }

        String[] argArray = cmd.getArgs();
        runBenchmarks(cmd);
        System.out.println("Benchmark run complete.");
    }

    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("storm-benchmark [options] <benchmark file>", options);
    }


    public static void runBenchmarks(CommandLine cmd) throws Exception {
        Yaml yaml = new Yaml();

        FileInputStream in = new FileInputStream((String) cmd.getArgList().get(0));

        Map<String, Object> suiteConf = (Map) yaml.load(in);
        in.close();

        ArrayList<Map<String, Object>> benchmarks = (ArrayList<Map<String, Object>>) suiteConf.get("benchmark-suite");
        System.out.println("Found " + benchmarks.size() + " benchmarks.");
        for (Map<String, Object> config : benchmarks) {
            if ((Boolean) config.get("benchmark.enabled") == true)
                runTest((String) config.get("topology.class"), config);
        }
    }

    private static void runTest(String topologyClass, Map<String, Object> benchmarkConfig) throws Exception {
        ArrayList<String> command = new ArrayList<String>();
        command.add("storm");
        command.add("jar");
        command.add("target/storm-benchmark-0.1.0-jar-with-dependencies.jar");
        command.add("Runner");
        command.add(topologyClass);
        addBenchmarkCommandOpts(command, benchmarkConfig);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        final Process proc = pb.start();
        Thread t = new Thread(new StreamRedirect(proc.getInputStream()));
        t.start();

        System.out.println("started process");
        int exitVal = proc.waitFor();
        System.out.println("exitVal=" + exitVal);

        killTopology(benchmarkConfig);
    }


    private static void killTopology(Map<String, Object> benchmarkConfig) throws Exception {

        Map<String, Object> topConfig = (Map<String, Object>) benchmarkConfig.get("topology.config");
//        String timeoutStr = (String) topConfig.get("topology.message.timeout.secs");
        String topologyName = (String) topConfig.get("topology.name");

        // TODO are we really that concerned about waiting for processing to complete? we could just shoot it right away.
//        int timeoutSecs = 30;
//        if(timeoutStr != null){
//            try{
//                timeoutSecs = Integer.parseInt(timeoutStr);
//            } catch(NumberFormatException e){
//            }
//        }

        String[] command = new String[]{"storm", "kill", topologyName, "-w", "5"};

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        final Process proc = pb.start();
        Thread t = new Thread(new StreamRedirect(proc.getInputStream()));
        t.start();

        System.out.println("Killing topology: " + topologyName);
        int exitVal = proc.waitFor();
        System.out.println("exitVal=" + exitVal);

        if (exitVal == 0) {
            System.out.println("Waiting for topology to complete.");
            Thread.sleep(5000);
        }
    }

    private static void addBenchmarkCommandOpts(ArrayList<String> cmd, Map<String, Object> config) {
        String[] opts = new String[]{"benchmark.poll.interval", "benchmark.runtime", "benchmark.report.dir", "benchmark.label"};
        for (String s : opts) {
            cmd.add("-c");
            cmd.add(s + "=" + config.get(s));
        }

        Map<String, Object> topConfig = (Map<String, Object>) config.get("topology.config");
        for (String s : topConfig.keySet()) {
            cmd.add("-c");
            cmd.add(s + "=" + topConfig.get(s));
        }
    }


    private static class StreamRedirect implements Runnable {
        private InputStream in;

        public StreamRedirect(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                System.out.println("Running");
                int i = -1;
                while ((i = this.in.read()) != -1) {
                    System.out.write(i);
                }
                this.in.close();
                System.out.println("Stream reader closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}