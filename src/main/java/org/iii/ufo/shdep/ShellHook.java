package org.iii.ufo.shdep;

import java.util.List;

@FunctionalInterface
public interface ShellHook {
	public boolean beforeScript(Script path, List<String> args);
}
