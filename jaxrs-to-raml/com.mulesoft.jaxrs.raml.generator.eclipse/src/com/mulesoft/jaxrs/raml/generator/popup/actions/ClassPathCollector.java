package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class ClassPathCollector {
	
	private static final String JVM_CONTAINER_SIGN = "org.eclipse.jdt.launching.JRE_CONTAINER";

	public static Collection<String> constructProjectClassPath(IProject _project, boolean includevmContainer){
		Collection<String> result = new LinkedHashSet<String>() ;
		constructProjectClassPath(_project, result, false, includevmContainer, -1) ;
		return result;
	}
	
	public static Collection<String> constructProjectClassPath(IProject _project, boolean includevmContainer, int depth){
		Collection<String> result = new LinkedHashSet<String>() ;
		constructProjectClassPath(_project, result, false, includevmContainer, depth) ;
		return result;
	}
	
	private static void constructProjectClassPath(IProject _project, Collection<String> result, boolean takeExportedOnly,
			boolean includevmContainer, int depth) {
		
		final IJavaProject project = JavaCore.create(_project) ;
		IPath outputLocation;
		try {
			outputLocation = project.getOutputLocation();
			String resolvedPath = resolveEntryPath(outputLocation, _project);
			if (resolvedPath != null) {
				result.add(resolvedPath);
			}			
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if( project == null )
			return ;
		
		try {
			IClasspathEntry[] rawClasspath = project.getRawClasspath();
			for (IClasspathEntry entry : rawClasspath)
			{
				if( takeExportedOnly && !entry.isExported() )
					continue;
				
				processEntry(project, result, entry, includevmContainer,depth);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return ;
	}
	
	private static void processEntry(IJavaProject project, Collection<String> result, IClasspathEntry entry,
			boolean includevmContainer, int depth)
	{
		IClasspathEntry classpathEntry = JavaCore.getResolvedClasspathEntry(entry);
		if (classpathEntry == null) {
			return ;
		}
		
		try {
			if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
			{
				IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
				if( container == null ){
					return;
				}
				
				//skipping JVM container if needed
				if (classpathEntry.getPath() != null &&
						classpathEntry.getPath().toString().contains(JVM_CONTAINER_SIGN)
						&& !includevmContainer) {
					return;
				}
				
				IClasspathEntry[] entries = container.getClasspathEntries();
				if (entries == null || entries.length == 0)
					return;
	
				for (IClasspathEntry ent : entries)
					processEntry(project, result, ent, includevmContainer,depth);
				
			}
			else if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				addEntryPathToResult(entry, project.getProject(), result);
			}
			else if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				IResource dependentProject = ResourcesPlugin.getWorkspace().getRoot().findMember(classpathEntry.getPath());
				
				if (dependentProject != null && dependentProject instanceof IProject && dependentProject.exists()) {
					IJavaProject dependentJavaProject = JavaCore.create((IProject)dependentProject);
					if (dependentJavaProject != null && dependentJavaProject.exists()) {
						IPath outputLocation = dependentJavaProject.getOutputLocation();
						if (outputLocation != null) {
							IResource resolvedOutputLocation = ResourcesPlugin.getWorkspace().getRoot().findMember(outputLocation);
							if (resolvedOutputLocation != null && resolvedOutputLocation.exists()) {
								result.add(resolvedOutputLocation.getLocation().toOSString());
							}
						}
					}
					if(depth!=0){
						constructProjectClassPath((IProject) dependentProject, result, true, includevmContainer,depth-1) ;
					}
				}
			}
			else if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				//we don't need source entries
				IPath outputLocation = classpathEntry.getOutputLocation();
				if (outputLocation!=null){
					String resolvedPath = resolveEntryPath(outputLocation, project.getProject());
					if (resolvedPath != null) {
						result.add(resolvedPath);
					}					
				}
			}
			else if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
				addEntryPathToResult(entry, project.getProject(), result);
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	private static void addEntryPathToResult(IClasspathEntry entry, IProject project, Collection<String> result) {
		IPath entryPath = entry.getPath();
		if (entryPath == null) {
			return;
		}
		
		String resolvedPath = resolveEntryPath(entryPath, project);
		if (resolvedPath != null) {
			result.add(resolvedPath);
		}
	}
	
	private static String resolveEntryPath(IPath entryPath, IProject project) {
		if (entryPath.toString().startsWith("..")
				|| entryPath.toString().startsWith(".")) {
			IPath projectAbsolutePath = project.getLocation();
			IPath absolutePathToResolve = projectAbsolutePath.append(entryPath);
			File f = new File(absolutePathToResolve.toOSString());
			if (f.exists()) {
				return f.getAbsolutePath();
			}
		}

		File f = new File( entryPath.toFile().getAbsolutePath() ) ;
		if( f.exists() ){
			return entryPath.toFile().getAbsolutePath() ;
		}

		IResource member = project.findMember(entryPath);
		if ( member != null ){
			
			File entryFile = member.getLocation().toFile();
			String absolutePath = entryFile.getAbsolutePath();
			return absolutePath;
		}
		else {
			String str = entryPath.segment(0) ;		
			try {
				IProject prj = ResourcesPlugin.getWorkspace().getRoot().getProject(str) ;
				
				if( prj != null ){
					IPath removeFirstSegments = entryPath.removeFirstSegments(1);
					member = prj.findMember(removeFirstSegments) ;
					
					if (member != null ){
						File entryFile = member.getLocation().toFile();
						String absolutePath = entryFile.getAbsolutePath();
						return absolutePath;
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}		
		return null;
	}
}
