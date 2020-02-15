package org.iii.ufo.shdep;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class ShellVar{
	public static class Var{
		//private final String name;
		private String value = "";    //may empty, should not null
		private boolean exported = false;
		private ShellVarFunc func = null;

		//public String getName() { return name; }

		public String getValue() { return value; }
		public void setValue(String value) { this.value = StringUtils.defaultString(value); }

		public boolean isExported() { return exported; }
		public void setExported(boolean exported) { this.exported = exported; }

		public ShellVarFunc getFunc() { return func; }
		public void setFunc(ShellVarFunc func) { this.func = func; }

		public Var(){}

		public Var(String value, boolean exported, ShellVarFunc func){
			//this.name = name;
			setValue(value);
			setExported(exported);
			setFunc(func);
		}
	}
	
	
	private final Map<String, Var> vars = new HashMap<>();
	
	public ShellVar(){
	}
	
	public boolean contains(String name){
		return vars.containsKey(name);
	}
	
	public String get(String name){
		Var var =  vars.get(name);
		if(var != null)
			return var.getValue();
		return null;
	}

	public Integer getAsInt(String name){
		String value = get(name);
		if(value != null)
			return Integer.valueOf(value);
		return null;
	}
	
	private Var getOrCreate(String name){
		Var var =  vars.get(name);
		if(var == null){
			var = new Var();
			vars.put(name, var);
		}
		
		return var;
	}

	// set export
	public void export(String name){
		getOrCreate(name).setExported(true);
	}

	//set func
	public void setFunc(String name, ShellVarFunc func){
		getOrCreate(name).setFunc(func);
	}
	
	// set =======================================================
	//set value, easy using interface
	public void set(String name, Object value){
		set(name, value.toString());
	}

	public void set(String name, String value){
		_set(name, value, null, null);
	}

	public void set(String name, String value, boolean exported){
		_set(name, value, exported, null);
	}

	//inner set(), not set field if null (so, the method CANNOT unset @func)
	private void _set(String name, String value, Boolean exported, ShellVarFunc func){
		Var var = getOrCreate(name);

		if(exported != null)
			var.setExported(exported);

		if(func != null)
			var.setFunc(func);

		if(value != null){
			var.setValue(value);
			
			//trigger func
			if(var.getFunc() != null)
				var.getFunc().run(name, value);
		}
	}

	//env helpers ========================================
	public void setEnv(Map<String, String> env){
		env.forEach((name, value) -> {
			set(name, value, true);
		});
	}
	
	public Map<String, String> getEnv(){
		return vars.entrySet().stream()
			.filter(e->e.getValue().isExported())
			.collect(Collectors.toMap(e->e.getKey(), e->e.getValue().getValue()));
	}

}
