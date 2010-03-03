package com.bridj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeEntities {
	static class CBInfo {
		long handle;
		int size;
		public CBInfo(long handle, int size) {
			this.handle = handle;
			this.size = size;
		}
		
	}
	Map<Class<?>, CBInfo> 
		functions = new HashMap<Class<?>, CBInfo>(),
		virtualMethods = new HashMap<Class<?>, CBInfo>(),
		getters = new HashMap<Class<?>, CBInfo>(),
		setters = new HashMap<Class<?>, CBInfo>(),
		javaToNativeCallbacks = new HashMap<Class<?>, CBInfo>(),
		objcMethodInfos = new HashMap<Class<?>, CBInfo>();
	
	//Map<Class<?>, long[]> getters;
	//Map<Class<?>, long[]> setters;
	
	public static class Builder {
		List<MethodCallInfo> 
			functionInfos = new ArrayList<MethodCallInfo>(),
			virtualMethods = new ArrayList<MethodCallInfo>(),
			javaToNativeCallbacks = new ArrayList<MethodCallInfo>(),
			getters = new ArrayList<MethodCallInfo>(),
			setters = new ArrayList<MethodCallInfo>(),
			objcMethodInfos = new ArrayList<MethodCallInfo>();
		//List<MethodCallInfo> getterInfos = new ArrayList<MethodCallInfo>();
		
		public void addFunction(MethodCallInfo info) {
			functionInfos.add(info);
		}
		public void addVirtualMethod(MethodCallInfo info) {
			virtualMethods.add(info);
		}
		public void addGetter(MethodCallInfo info) {
			getters.add(info);
		}
		public void addSetter(MethodCallInfo info) {
			setters.add(info);
		}
		public void addJavaToNativeCallback(MethodCallInfo info) {
			javaToNativeCallbacks.add(info);
		}
		public void addMethodFunction(MethodCallInfo info) {
			functionInfos.add(info);
		}
		public void addObjCMethod(MethodCallInfo info) {
			objcMethodInfos.add(info);
		}
	}
	
	
	public void release() {
		for (CBInfo callbacks : functions.values())
		    JNI.freeCFunctionBindings(callbacks.handle, callbacks.size);
		
		for (CBInfo callbacks : javaToNativeCallbacks.values())
		    JNI.freeJavaToCCallbacks(callbacks.handle, callbacks.size);

		for (CBInfo callbacks : virtualMethods.values())
		    JNI.freeVirtualMethodBindings(callbacks.handle, callbacks.size);
		
		for (CBInfo callbacks : objcMethodInfos.values())
		    JNI.freeObjCMethodBindings(callbacks.handle, callbacks.size);
	}
	@Override
	public void finalize() {
		release();
	}
	public void addDefinitions(Class<?> type, Builder builder) {
		int n;
        try {

		n = builder.functionInfos.size();
		if (n != 0)
			functions.put(type, new CBInfo(JNI.bindJavaMethodsToCFunctions(builder.functionInfos.toArray(new MethodCallInfo[n])), n));

		n = builder.virtualMethods.size();
		if (n != 0)
			virtualMethods.put(type, new CBInfo(JNI.bindJavaMethodsToVirtualMethods(builder.virtualMethods.toArray(new MethodCallInfo[n])), n));

		n = builder.javaToNativeCallbacks.size();
		if (n != 0)
			javaToNativeCallbacks.put(type, new CBInfo(JNI.bindJavaToCCallbacks(builder.javaToNativeCallbacks.toArray(new MethodCallInfo[n])), n));

		n = builder.objcMethodInfos.size();
		if (n != 0)
			objcMethodInfos.put(type, new CBInfo(JNI.bindJavaMethodsToObjCMethods(builder.objcMethodInfos.toArray(new MethodCallInfo[n])), n));

//		n = builder.setters.size();
//		if (n != 0)
//			setters.put(type, new CBInfo(JNI.bindFieldGetters(builder.setters.toArray(new MethodCallInfo[n])), n));

        } catch (Throwable th) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to add native definitions for class " + type.getName(), th);
        }
	}
}