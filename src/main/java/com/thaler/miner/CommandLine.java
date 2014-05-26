package com.thaler.miner;

import org.kohsuke.args4j.Option;

public class CommandLine {

	@Option(name = "--help", usage = "Print Help", aliases = { "--help", "-h" })
	private boolean help;

	@Option(name = "--noOperation", usage = "Do Nothing (Testing only)", aliases = { "-noop" })
	private boolean doNothing = false;


	public boolean isDoNothing() {
		return doNothing;
	}

	public boolean isHelp() {
		return help;
	}


	public void setDoNothing(final boolean doNothing) {
		this.doNothing = doNothing;
	}

	public void setHelp(final boolean help) {
		this.help = help;
	}


}
