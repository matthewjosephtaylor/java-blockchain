package com.thaler.miner;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * An awesome miner
 * 
 */
public class Main
{
	private static final Logger logger = Logger.getLogger(Main.class);
	private static final CommandLine commandLine = new CommandLine();
	private static CmdLineParser parser;

	public static void main(final String[] args) throws Exception
	{
		SLF4JBridgeHandler.install();
		logger.info("Application v" + Main.class.getPackage().getImplementationVersion());
		logger.info("Build: " + ResourceBundle.getBundle("buildInfo").getString("buildNumber"));
		parseCommandLine(args);
	}

	private static void parseCommandLine(final String[] args) {
		parser = new CmdLineParser(commandLine);
		try {
			parser.parseArgument(args);
			if (commandLine.isHelp()) {
				printUsageAndExit();
			}

		} catch (final CmdLineException e) {
			logger.error(e);
			printUsageAndExit();
		}
	}

	private static void printUsageAndExit() {
		System.out.println("usage: java -jar <name-of-jar> [options...]");
		parser.printUsage(System.out);
		System.exit(-1);
	}
}
