package org.iii.ufo.shdep;

import java.util.List;

@FunctionalInterface
public interface ScriptDepAction{
	
	public void act(List<FsPath> paths);

}
