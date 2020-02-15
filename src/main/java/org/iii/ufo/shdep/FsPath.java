package org.iii.ufo.shdep;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FsPath {
	public static FsPath fromFull(Path fsroot, Path fullPath){
		Path inner = combine("/", fsroot.relativize(fullPath));   //ensure in abs format
		return new FsPath(fullPath, inner);
	}

	public static FsPath of(Path fsroot, String innerPath){
		Path inner = combine("/", innerPath);   //ensure in abs format
		Path full = combine(fsroot, innerPath);
		return new FsPath(full, inner);
	}

	public static FsPath of(Path fsroot, Path innerPath){
		Path inner = combine("/", innerPath);   //ensure in abs format
		Path full = combine(fsroot, innerPath);
		return new FsPath(full, inner);
	}

	//The path.resolve(other), which would skip @path if @other is absolute form
	private static Path combine(Path p1, Path p2){
		return Paths.get(p1.toString(), p2.toString()).normalize();
	}
	
	private static Path combine(Path p1, String p2){
		return Paths.get(p1.toString(), p2).normalize();
	}

	private static Path combine(String p1, Path p2){
		return Paths.get(p1, p2.toString()).normalize();
	}

	private static Path combine(String p1, String p2){
		return Paths.get(p1, p2).normalize();
	}
	
	// ==================================================================

	private final Path innerPath;
	private final Path fullPath;
	
	private FsPath(Path fullPath, Path innerPath){
		this.fullPath = fullPath;
		this.innerPath = innerPath;
	}
	
	//access =======================================

	//the method is rare used, just infer
	public Path getFsroot() {
		String full = fullPath.toString();
		String inner = innerPath.toString();
		if(!full.endsWith(inner))
			throw new RuntimeException("logic error: full path '" + full + "' not ends with inner path '" + inner  + "'.");
		String fsroot = full.substring(0, full.length() - inner.length());
		return Paths.get(fsroot);
	}

	public Path getInnerPath() {
		return innerPath;
	}

	//a workaround for freemarker
	public String getInnerPathStr() {
		return innerPath.toString();
	}

	//a workaround for freemarker
	public File getFullPathFile() {
		return fullPath.toFile();
	}

	public Path getFullPath(){
		return fullPath;
	}
	
	public FsPath append(Path appPath){
		Path full = combine(fullPath, appPath);
		Path inner = combine(innerPath, appPath);
		return new FsPath(full, inner);
	}

	public FsPath append(String appPath){
		Path full = combine(fullPath, appPath);
		Path inner = combine(innerPath, appPath);
		return new FsPath(full, inner);
	}

	//helpers ====================================
	public boolean exists(){
		return Files.exists(fullPath);
	}

	public boolean isRegularFile(){
		return Files.isRegularFile(fullPath);
	}
	
	public boolean isDirectory(){
		return Files.isDirectory(fullPath);
	}
	
	// override ==================================

	@Override
	public String toString(){
		return fullPath.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof FsPath))
			return false;
		
		FsPath that = (FsPath)obj;
		return Objects.equals(this.fullPath, that.fullPath) &&
				Objects.equals(this.innerPath, that.innerPath);
	}

	@Override
	public int hashCode(){
		return Objects.hash(fullPath, innerPath);

	}

}
