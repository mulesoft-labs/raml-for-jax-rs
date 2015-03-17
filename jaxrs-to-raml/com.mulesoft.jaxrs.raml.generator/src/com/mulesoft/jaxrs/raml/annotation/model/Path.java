package com.mulesoft.jaxrs.raml.annotation.model;

import java.io.File;
import java.util.Arrays;

/**
 * The standard implementation of the <code>Path</code> interface.
 * Paths are always maintained in canonicalized form.  That is, parent
 * references (i.e., <code>../../</code>) and duplicate separators are
 * resolved.  For example,
 * <pre>     new Path("/a/b").append("../foo/bar")</pre>
 * will yield the path
 * <pre>     /a/foo/bar</pre>
 * <p>
 * This class can be used without OSGi running.
 * </p><p>
 * This class is not intended to be subclassed by clients but
 * may be instantiated.
 * </p>
 *
 * @see Path
 * @author kor
 * @version $Id: $Id
 */
public class Path implements  Cloneable {
	

	/**
	 * Path separator character constant "/" used in paths.
	 */
	public static final char SEPARATOR = '/';

	/** 
	 * Device separator character constant ":" used in paths.
	 */
	public static final char DEVICE_SEPARATOR = ':';


	/** masks for separator values */
	private static final int HAS_LEADING = 1;
	private static final int IS_UNC = 2;
	private static final int HAS_TRAILING = 4;

	private static final int ALL_SEPARATORS = HAS_LEADING | IS_UNC | HAS_TRAILING;

	/** Constant empty string value. */
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/** Constant value indicating no segments */
	private static final String[] NO_SEGMENTS = new String[0];

	/** Constant value containing the empty path with no device. */
	public static final Path EMPTY = new Path(EMPTY_STRING);

	/** Mask for all bits that are involved in the hash code */
	private static final int HASH_MASK = ~HAS_TRAILING;

	/** Constant root path string (<code>"/"</code>). */
	private static final String ROOT_STRING = "/"; //$NON-NLS-1$

	/** Constant value containing the root path with no device. */
	public static final Path ROOT = new Path(ROOT_STRING);

	/** Constant value indicating if the current platform is Windows */
	private static final boolean WINDOWS = java.io.File.separatorChar == '\\';

	/** The device id string. May be null if there is no device. */
	private String device = null;

	//Private implementation note: the segments and separators 
	//arrays are never modified, so that they can be shared between 
	//path instances

	/** The path segments */
	private String[] segments;

	/** flags indicating separators (has leading, is UNC, has trailing) */
	private int separators;

	/**
	 * Constructs a new path from the given string path.
	 * The string path must represent a valid file system path
	 * on the local file system.
	 * The path is canonicalized and double slashes are removed
	 * except at the beginning. (to handle UNC paths). All forward
	 * slashes ('/') are treated as segment delimiters, and any
	 * segment and device delimiters for the local file system are
	 * also respected.
	 *
	 * @param pathString the portable string path
	 * @see Path#toPortableString()
	 * @since 3.1
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public static Path fromOSString(String pathString) {
		return new Path(pathString);
	}

	/**
	 * Constructs a new path from the given path string.
	 * The path string must have been produced by a previous
	 * call to <code>Path.toPortableString</code>.
	 *
	 * @param pathString the portable path string
	 * @see Path#toPortableString()
	 * @since 3.1
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public static Path fromPortableString(String pathString) {
		int firstMatch = pathString.indexOf(DEVICE_SEPARATOR) + 1;
		//no extra work required if no device characters
		if (firstMatch <= 0)
			return new Path().initialize(null, pathString);
		//if we find a single colon, then the path has a device
		String devicePart = null;
		int pathLength = pathString.length();
		if (firstMatch == pathLength || pathString.charAt(firstMatch) != DEVICE_SEPARATOR) {
			devicePart = pathString.substring(0, firstMatch);
			pathString = pathString.substring(firstMatch, pathLength);
		}
		//optimize for no colon literals
		if (pathString.indexOf(DEVICE_SEPARATOR) == -1)
			return new Path().initialize(devicePart, pathString);
		//contract colon literals
		char[] chars = pathString.toCharArray();
		int readOffset = 0, writeOffset = 0, length = chars.length;
		while (readOffset < length) {
			if (chars[readOffset] == DEVICE_SEPARATOR)
				if (++readOffset >= length)
					break;
			chars[writeOffset++] = chars[readOffset++];
		}
		return new Path().initialize(devicePart, new String(chars, 0, writeOffset));
	}

	/* (Intentionally not included in javadoc)
	 * Private constructor.
	 */
	private Path() {
		// not allowed
	}

	/**
	 * Constructs a new path from the given string path.
	 * The string path must represent a valid file system path
	 * on the local file system.
	 * The path is canonicalized and double slashes are removed
	 * except at the beginning. (to handle UNC paths). All forward
	 * slashes ('/') are treated as segment delimiters, and any
	 * segment and device delimiters for the local file system are
	 * also respected (such as colon (':') and backslash ('\') on some file systems).
	 *
	 * @param fullPath the string path
	 * @see #isValidPath(String)
	 */
	public Path(String fullPath) {
		String devicePart = null;
		if (WINDOWS) {
			//convert backslash to forward slash
			fullPath = fullPath.indexOf('\\') == -1 ? fullPath : fullPath.replace('\\', SEPARATOR);
			//extract device
			int i = fullPath.indexOf(DEVICE_SEPARATOR);
			if (i != -1) {
				//remove leading slash from device part to handle output of URL.getFile()
				int start = fullPath.charAt(0) == SEPARATOR ? 1 : 0;
				devicePart = fullPath.substring(start, i + 1);
				fullPath = fullPath.substring(i + 1, fullPath.length());
			}
		}
		initialize(devicePart, fullPath);
	}

	/**
	 * Constructs a new path from the given device id and string path.
	 * The given string path must be valid.
	 * The path is canonicalized and double slashes are removed except
	 * at the beginning (to handle UNC paths). All forward
	 * slashes ('/') are treated as segment delimiters, and any
	 * segment delimiters for the local file system are
	 * also respected (such as backslash ('\') on some file systems).
	 *
	 * @param device the device id
	 * @param path the string path
	 * @see #isValidPath(String)
	 * @see #setDevice(String)
	 * @see #isValidPath(String)
	 * @see #setDevice(String)
	 */
	public Path(String device, String path) {
		if (WINDOWS) {
			//convert backslash to forward slash
			path = path.indexOf('\\') == -1 ? path : path.replace('\\', SEPARATOR);
		}
		initialize(device, path);
	}

	/* (Intentionally not included in javadoc)
	 * Private constructor.
	 */
	private Path(String device, String[] segments, int _separators) {
		// no segment validations are done for performance reasons	
		this.segments = segments;
		this.device = device;
		//hash code is cached in all but the bottom three bits of the separators field
		this.separators = (computeHashCode() << 3) | (_separators & ALL_SEPARATORS);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#addFileExtension
	 */
	/**
	 * <p>addFileExtension.</p>
	 *
	 * @param extension a {@link java.lang.String} object.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path addFileExtension(String extension) {
		if (isRoot() || isEmpty() || hasTrailingSeparator())
			return this;
		int len = segments.length;
		String[] newSegments = new String[len];
		System.arraycopy(segments, 0, newSegments, 0, len - 1);
		newSegments[len - 1] = segments[len - 1] + '.' + extension;
		return new Path(device, newSegments, separators);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#addTrailingSeparator
	 */
	/**
	 * <p>addTrailingSeparator.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path addTrailingSeparator() {
		if (hasTrailingSeparator() || isRoot()) {
			return this;
		}
		//XXX workaround, see 1GIGQ9V
		if (isEmpty()) {
			return new Path(device, segments, HAS_LEADING);
		}
		return new Path(device, segments, separators | HAS_TRAILING);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#append(Path)
	 */
	/**
	 * <p>append.</p>
	 *
	 * @param tail a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path append(Path tail) {
		//optimize some easy cases
		if (tail == null || tail.segmentCount() == 0)
			return this;
		//these call chains look expensive, but in most cases they are no-ops
		if (this.isEmpty())
			return tail.setDevice(device).makeRelative().makeUNC(isUNC());
		if (this.isRoot())
			return tail.setDevice(device).makeAbsolute().makeUNC(isUNC());

		//concatenate the two segment arrays
		int myLen = segments.length;
		int tailLen = tail.segmentCount();
		String[] newSegments = new String[myLen + tailLen];
		System.arraycopy(segments, 0, newSegments, 0, myLen);
		for (int i = 0; i < tailLen; i++) {
			newSegments[myLen + i] = tail.segment(i);
		}
		//use my leading separators and the tail's trailing separator
		Path result = new Path(device, newSegments, (separators & (HAS_LEADING | IS_UNC)) | (tail.hasTrailingSeparator() ? HAS_TRAILING : 0));
		String tailFirstSegment = newSegments[myLen];
		if (tailFirstSegment.equals("..") || tailFirstSegment.equals(".")) { //$NON-NLS-1$ //$NON-NLS-2$
			result.canonicalize();
		}
		return result;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#append(java.lang.String)
	 */
	/**
	 * <p>append.</p>
	 *
	 * @param tail a {@link java.lang.String} object.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path append(String tail) {
		//optimize addition of a single segment
		if (tail.indexOf(SEPARATOR) == -1 && tail.indexOf("\\") == -1 && tail.indexOf(DEVICE_SEPARATOR) == -1) { //$NON-NLS-1$
			int tailLength = tail.length();
			if (tailLength < 3) {
				//some special cases
				if (tailLength == 0 || ".".equals(tail)) { //$NON-NLS-1$
					return this;
				}
				if ("..".equals(tail)) //$NON-NLS-1$
					return removeLastSegments(1);
			}
			//just add the segment
			int myLen = segments.length;
			String[] newSegments = new String[myLen + 1];
			System.arraycopy(segments, 0, newSegments, 0, myLen);
			newSegments[myLen] = tail;
			return new Path(device, newSegments, separators & ~HAS_TRAILING);
		}
		//go with easy implementation
		return append(new Path(tail));
	}

	/**
	 * Destructively converts this path to its canonical form.
	 * <p>
	 * In its canonical form, a path does not have any
	 * "." segments, and parent references ("..") are collapsed
	 * where possible.
	 * </p>
	 * @return true if the path was modified, and false otherwise.
	 */
	private boolean canonicalize() {
		//look for segments that need canonicalizing
		for (int i = 0, max = segments.length; i < max; i++) {
			String segment = segments[i];
			if (segment.charAt(0) == '.' && (segment.equals("..") || segment.equals("."))) { //$NON-NLS-1$ //$NON-NLS-2$
				//path needs to be canonicalized
				collapseParentReferences();
				//paths of length 0 have no trailing separator
				if (segments.length == 0)
					separators &= (HAS_LEADING | IS_UNC);
				//recompute hash because canonicalize affects hash
				separators = (separators & ALL_SEPARATORS) | (computeHashCode() << 3);
				return true;
			}
		}
		return false;
	}

	/* (Intentionally not included in javadoc)
	 * Clones this object.
	 */
	/**
	 * <p>clone.</p>
	 *
	 * @return a {@link java.lang.Object} object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Destructively removes all occurrences of ".." segments from this path.
	 */
	private void collapseParentReferences() {
		int segmentCount = segments.length;
		String[] stack = new String[segmentCount];
		int stackPointer = 0;
		for (int i = 0; i < segmentCount; i++) {
			String segment = segments[i];
			if (segment.equals("..")) { //$NON-NLS-1$
				if (stackPointer == 0) {
					// if the stack is empty we are going out of our scope 
					// so we need to accumulate segments.  But only if the original
					// path is relative.  If it is absolute then we can't go any higher than
					// root so simply toss the .. references.
					if (!isAbsolute())
						stack[stackPointer++] = segment; //stack push
				} else {
					// if the top is '..' then we are accumulating segments so don't pop
					if ("..".equals(stack[stackPointer - 1])) //$NON-NLS-1$
						stack[stackPointer++] = ".."; //$NON-NLS-1$
					else
						stackPointer--;
					//stack pop
				}
				//collapse current references
			} else if (!segment.equals(".") || segmentCount == 1) //$NON-NLS-1$
				stack[stackPointer++] = segment; //stack push
		}
		//if the number of segments hasn't changed, then no modification needed
		if (stackPointer == segmentCount)
			return;
		//build the new segment array backwards by popping the stack
		String[] newSegments = new String[stackPointer];
		System.arraycopy(stack, 0, newSegments, 0, stackPointer);
		this.segments = newSegments;
	}

	/**
	 * Removes duplicate slashes from the given path, with the exception
	 * of leading double slash which represents a UNC path.
	 */
	private String collapseSlashes(String path) {
		int length = path.length();
		// if the path is only 0, 1 or 2 chars long then it could not possibly have illegal
		// duplicate slashes.
		if (length < 3)
			return path;
		// check for an occurrence of // in the path.  Start at index 1 to ensure we skip leading UNC //
		// If there are no // then there is nothing to collapse so just return.
		if (path.indexOf("//", 1) == -1) //$NON-NLS-1$
			return path;
		// We found an occurrence of // in the path so do the slow collapse.
		char[] result = new char[path.length()];
		int count = 0;
		boolean hasPrevious = false;
		char[] characters = path.toCharArray();
		for (int index = 0; index < characters.length; index++) {
			char c = characters[index];
			if (c == SEPARATOR) {
				if (hasPrevious) {
					// skip double slashes, except for beginning of UNC.
					// note that a UNC path can't have a device.
					if (device == null && index == 1) {
						result[count] = c;
						count++;
					}
				} else {
					hasPrevious = true;
					result[count] = c;
					count++;
				}
			} else {
				hasPrevious = false;
				result[count] = c;
				count++;
			}
		}
		return new String(result, 0, count);
	}

	/* (Intentionally not included in javadoc)
	 * Computes the hash code for this object.
	 */
	private int computeHashCode() {
		int hash = device == null ? 17 : device.hashCode();
		int segmentCount = segments.length;
		for (int i = 0; i < segmentCount; i++) {
			//this function tends to given a fairly even distribution
			hash = hash * 37 + segments[i].hashCode();
		}
		return hash;
	}

	/* (Intentionally not included in javadoc)
	 * Returns the size of the string that will be created by toString or toOSString.
	 */
	private int computeLength() {
		int length = 0;
		if (device != null)
			length += device.length();
		if ((separators & HAS_LEADING) != 0)
			length++;
		if ((separators & IS_UNC) != 0)
			length++;
		//add the segment lengths
		int max = segments.length;
		if (max > 0) {
			for (int i = 0; i < max; i++) {
				length += segments[i].length();
			}
			//add the separator lengths
			length += max - 1;
		}
		if ((separators & HAS_TRAILING) != 0)
			length++;
		return length;
	}

	/* (Intentionally not included in javadoc)
	 * Returns the number of segments in the given path
	 */
	private int computeSegmentCount(String path) {
		int len = path.length();
		if (len == 0 || (len == 1 && path.charAt(0) == SEPARATOR)) {
			return 0;
		}
		int count = 1;
		int prev = -1;
		int i;
		while ((i = path.indexOf(SEPARATOR, prev + 1)) != -1) {
			if (i != prev + 1 && i != len) {
				++count;
			}
			prev = i;
		}
		if (path.charAt(len - 1) == SEPARATOR) {
			--count;
		}
		return count;
	}

	/**
	 * Computes the segment array for the given canonicalized path.
	 */
	private String[] computeSegments(String path) {
		// performance sensitive --- avoid creating garbage
		int segmentCount = computeSegmentCount(path);
		if (segmentCount == 0)
			return NO_SEGMENTS;
		String[] newSegments = new String[segmentCount];
		int len = path.length();
		// check for initial slash
		int firstPosition = (path.charAt(0) == SEPARATOR) ? 1 : 0;
		// check for UNC
		if (firstPosition == 1 && len > 1 && (path.charAt(1) == SEPARATOR))
			firstPosition = 2;
		int lastPosition = (path.charAt(len - 1) != SEPARATOR) ? len - 1 : len - 2;
		// for non-empty paths, the number of segments is 
		// the number of slashes plus 1, ignoring any leading
		// and trailing slashes
		int next = firstPosition;
		for (int i = 0; i < segmentCount; i++) {
			int start = next;
			int end = path.indexOf(SEPARATOR, next);
			if (end == -1) {
				newSegments[i] = path.substring(start, lastPosition + 1);
			} else {
				newSegments[i] = path.substring(start, end);
			}
			next = end + 1;
		}
		return newSegments;
	}

	/**
	 * Returns the platform-neutral encoding of the given segment onto
	 * the given string buffer. This escapes literal colon characters with double colons.
	 */
	private void encodeSegment(String string, StringBuffer buf) {
		int len = string.length();
		for (int i = 0; i < len; i++) {
			char c = string.charAt(i);
			buf.append(c);
			if (c == DEVICE_SEPARATOR)
				buf.append(DEVICE_SEPARATOR);
		}
	}

	/* (Intentionally not included in javadoc)
	 * Compares objects for equality.
	 */
	/** {@inheritDoc} */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Path))
			return false;
		Path target = (Path) obj;
		//check leading separators and hash code
		if ((separators & HASH_MASK) != (target.separators & HASH_MASK))
			return false;
		String[] targetSegments = target.segments;
		int i = segments.length;
		//check segment count
		if (i != targetSegments.length)
			return false;
		//check segments in reverse order - later segments more likely to differ
		while (--i >= 0)
			if (!segments[i].equals(targetSegments[i]))
				return false;
		//check device last (least likely to differ)
		return device == target.device || (device != null && device.equals(target.device));
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#getDevice
	 */
	/**
	 * <p>Getter for the field <code>device</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDevice() {
		return device;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#getFileExtension
	 */
	/**
	 * <p>getFileExtension.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFileExtension() {
		if (hasTrailingSeparator()) {
			return null;
		}
		String lastSegment = lastSegment();
		if (lastSegment == null) {
			return null;
		}
		int index = lastSegment.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		return lastSegment.substring(index + 1);
	}

	/* (Intentionally not included in javadoc)
	 * Computes the hash code for this object.
	 */
	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		return separators & HASH_MASK;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#hasTrailingSeparator2
	 */
	/**
	 * <p>hasTrailingSeparator.</p>
	 *
	 * @return a boolean.
	 */
	public boolean hasTrailingSeparator() {
		return (separators & HAS_TRAILING) != 0;
	}

	/*
	 * Initialize the current path with the given string.
	 */
	private Path initialize(String deviceString, String path) {
		this.device = deviceString;

		path = collapseSlashes(path);
		int len = path.length();

		//compute the separators array
		if (len < 2) {
			if (len == 1 && path.charAt(0) == SEPARATOR) {
				this.separators = HAS_LEADING;
			} else {
				this.separators = 0;
			}
		} else {
			boolean hasLeading = path.charAt(0) == SEPARATOR;
			boolean isUNC = hasLeading && path.charAt(1) == SEPARATOR;
			//UNC path of length two has no trailing separator
			boolean hasTrailing = !(isUNC && len == 2) && path.charAt(len - 1) == SEPARATOR;
			separators = hasLeading ? HAS_LEADING : 0;
			if (isUNC)
				separators |= IS_UNC;
			if (hasTrailing)
				separators |= HAS_TRAILING;
		}
		//compute segments and ensure canonical form
		segments = computeSegments(path);
		if (!canonicalize()) {
			//compute hash now because canonicalize didn't need to do it
			separators = (separators & ALL_SEPARATORS) | (computeHashCode() << 3);
		}
		return this;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#isAbsolute
	 */
	/**
	 * <p>isAbsolute.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isAbsolute() {
		//it's absolute if it has a leading separator
		return (separators & HAS_LEADING) != 0;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#isEmpty
	 */
	/**
	 * <p>isEmpty.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEmpty() {
		//true if no segments and no leading prefix
		return segments.length == 0 && ((separators & ALL_SEPARATORS) != HAS_LEADING);

	}

	/* (Intentionally not included in javadoc)
	 * @see Path#isPrefixOf
	 */
	/**
	 * <p>isPrefixOf.</p>
	 *
	 * @param anotherPath a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 * @return a boolean.
	 */
	public boolean isPrefixOf(Path anotherPath) {
		if (device == null) {
			if (anotherPath.getDevice() != null) {
				return false;
			}
		} else {
			if (!device.equalsIgnoreCase(anotherPath.getDevice())) {
				return false;
			}
		}
		if (isEmpty() || (isRoot() && anotherPath.isAbsolute())) {
			return true;
		}
		int len = segments.length;
		if (len > anotherPath.segmentCount()) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			if (!segments[i].equals(anotherPath.segment(i)))
				return false;
		}
		return true;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#isRoot
	 */
	/**
	 * <p>isRoot.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isRoot() {
		//must have no segments, a leading separator, and not be a UNC path.
		return this == ROOT || (segments.length == 0 && ((separators & ALL_SEPARATORS) == HAS_LEADING));
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#isUNC
	 */
	/**
	 * <p>isUNC.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isUNC() {
		if (device != null)
			return false;
		return (separators & IS_UNC) != 0;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#isValidPath(String)
	 */
	/**
	 * <p>isValidPath.</p>
	 *
	 * @param path a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean isValidPath(String path) {
		Path test = new Path(path);
		for (int i = 0, max = test.segmentCount(); i < max; i++)
			if (!isValidSegment(test.segment(i)))
				return false;
		return true;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#isValidSegment(String)
	 */
	/**
	 * <p>isValidSegment.</p>
	 *
	 * @param segment a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean isValidSegment(String segment) {
		int size = segment.length();
		if (size == 0)
			return false;
		for (int i = 0; i < size; i++) {
			char c = segment.charAt(i);
			if (c == '/')
				return false;
			if (WINDOWS && (c == '\\' || c == ':'))
				return false;
		}
		return true;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#lastSegment()
	 */
	/**
	 * <p>lastSegment.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String lastSegment() {
		int len = segments.length;
		return len == 0 ? null : segments[len - 1];
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#makeAbsolute()
	 */
	/**
	 * <p>makeAbsolute.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path makeAbsolute() {
		if (isAbsolute()) {
			return this;
		}
		Path result = new Path(device, segments, separators | HAS_LEADING);
		//may need canonicalizing if it has leading ".." or "." segments
		if (result.segmentCount() > 0) {
			String first = result.segment(0);
			if (first.equals("..") || first.equals(".")) { //$NON-NLS-1$ //$NON-NLS-2$
				result.canonicalize();
			}
		}
		return result;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#makeRelative()
	 */
	/**
	 * <p>makeRelative.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path makeRelative() {
		if (!isAbsolute()) {
			return this;
		}
		return new Path(device, segments, separators & HAS_TRAILING);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since org.eclipse.equinox.common 3.5
	 * @param base a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path makeRelativeTo(Path base) {
		//can't make relative if devices are not equal
		if (device != base.getDevice() && (device == null || !device.equalsIgnoreCase(base.getDevice())))
			return this;
		int commonLength = matchingFirstSegments(base);
		final int differenceLength = base.segmentCount() - commonLength;
		final int newSegmentLength = differenceLength + segmentCount() - commonLength;
		if (newSegmentLength == 0)
			return Path.EMPTY;
		String[] newSegments = new String[newSegmentLength];
		//add parent references for each segment different from the base
		Arrays.fill(newSegments, 0, differenceLength, ".."); //$NON-NLS-1$
		//append the segments of this path not in common with the base
		System.arraycopy(segments, commonLength, newSegments, differenceLength, newSegmentLength - differenceLength);
		return new Path(null, newSegments, separators & HAS_TRAILING);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#makeUNC(boolean)
	 */
	/**
	 * <p>makeUNC.</p>
	 *
	 * @param toUNC a boolean.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path makeUNC(boolean toUNC) {
		// if we are already in the right form then just return
		if (!(toUNC ^ isUNC()))
			return this;

		int newSeparators = this.separators;
		if (toUNC) {
			newSeparators |= HAS_LEADING | IS_UNC;
		} else {
			//mask out the UNC bit
			newSeparators &= HAS_LEADING | HAS_TRAILING;
		}
		return new Path(toUNC ? null : device, segments, newSeparators);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#matchingFirstSegments(Path)
	 */
	/**
	 * <p>matchingFirstSegments.</p>
	 *
	 * @param anotherPath a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 * @return a int.
	 */
	public int matchingFirstSegments(Path anotherPath) {
		int anotherPathLen = anotherPath.segmentCount();
		int max = Math.min(segments.length, anotherPathLen);
		int count = 0;
		for (int i = 0; i < max; i++) {
			if (!segments[i].equals(anotherPath.segment(i))) {
				return count;
			}
			count++;
		}
		return count;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#removeFileExtension()
	 */
	/**
	 * <p>removeFileExtension.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path removeFileExtension() {
		String extension = getFileExtension();
		if (extension == null || extension.equals("")) { //$NON-NLS-1$
			return this;
		}
		String lastSegment = lastSegment();
		int index = lastSegment.lastIndexOf(extension) - 1;
		return removeLastSegments(1).append(lastSegment.substring(0, index));
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#removeFirstSegments(int)
	 */
	/**
	 * <p>removeFirstSegments.</p>
	 *
	 * @param count a int.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path removeFirstSegments(int count) {
		if (count == 0)
			return this;
		if (count >= segments.length) {
			return new Path(device, NO_SEGMENTS, 0);
		}
		int newSize = segments.length - count;
		String[] newSegments = new String[newSize];
		System.arraycopy(this.segments, count, newSegments, 0, newSize);

		//result is always a relative path
		return new Path(device, newSegments, separators & HAS_TRAILING);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#removeLastSegments(int)
	 */
	/**
	 * <p>removeLastSegments.</p>
	 *
	 * @param count a int.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path removeLastSegments(int count) {
		if (count == 0)
			return this;
		if (count >= segments.length) {
			//result will have no trailing separator
			return new Path(device, NO_SEGMENTS, separators & (HAS_LEADING | IS_UNC));
		}
		int newSize = segments.length - count;
		String[] newSegments = new String[newSize];
		System.arraycopy(this.segments, 0, newSegments, 0, newSize);
		return new Path(device, newSegments, separators);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#removeTrailingSeparator()
	 */
	/**
	 * <p>removeTrailingSeparator.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path removeTrailingSeparator() {
		if (!hasTrailingSeparator()) {
			return this;
		}
		return new Path(device, segments, separators & (HAS_LEADING | IS_UNC));
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#segment(int)
	 */
	/**
	 * <p>segment.</p>
	 *
	 * @param index a int.
	 * @return a {@link java.lang.String} object.
	 */
	public String segment(int index) {
		if (index >= segments.length)
			return null;
		return segments[index];
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#segmentCount()
	 */
	/**
	 * <p>segmentCount.</p>
	 *
	 * @return a int.
	 */
	public int segmentCount() {
		return segments.length;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#segments()
	 */
	/**
	 * <p>segments.</p>
	 *
	 * @return an array of {@link java.lang.String} objects.
	 */
	public String[] segments() {
		String[] segmentCopy = new String[segments.length];
		System.arraycopy(segments, 0, segmentCopy, 0, segments.length);
		return segmentCopy;
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#setDevice(String)
	 */
	/**
	 * <p>Setter for the field <code>device</code>.</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path setDevice(String value) {
		
		//return the receiver if the device is the same
		if (value == device || (value != null && value.equals(device)))
			return this;

		return new Path(value, segments, separators);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#toFile()
	 */
	/**
	 * <p>toFile.</p>
	 *
	 * @return a {@link java.io.File} object.
	 */
	public File toFile() {
		return new File(toOSString());
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#toOSString()
	 */
	/**
	 * <p>toOSString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toOSString() {
		//Note that this method is identical to toString except
		//it uses the OS file separator instead of the path separator
		int resultSize = computeLength();
		if (resultSize <= 0)
			return EMPTY_STRING;
		char FILE_SEPARATOR = File.separatorChar;
		char[] result = new char[resultSize];
		int offset = 0;
		if (device != null) {
			int size = device.length();
			device.getChars(0, size, result, offset);
			offset += size;
		}
		if ((separators & HAS_LEADING) != 0)
			result[offset++] = FILE_SEPARATOR;
		if ((separators & IS_UNC) != 0)
			result[offset++] = FILE_SEPARATOR;
		int len = segments.length - 1;
		if (len >= 0) {
			//append all but the last segment, with separators
			for (int i = 0; i < len; i++) {
				int size = segments[i].length();
				segments[i].getChars(0, size, result, offset);
				offset += size;
				result[offset++] = FILE_SEPARATOR;
			}
			//append the last segment
			int size = segments[len].length();
			segments[len].getChars(0, size, result, offset);
			offset += size;
		}
		if ((separators & HAS_TRAILING) != 0)
			result[offset++] = FILE_SEPARATOR;
		return new String(result);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#toPortableString()
	 */
	/**
	 * <p>toPortableString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toPortableString() {
		int resultSize = computeLength();
		if (resultSize <= 0)
			return EMPTY_STRING;
		StringBuffer result = new StringBuffer(resultSize);
		if (device != null)
			result.append(device);
		if ((separators & HAS_LEADING) != 0)
			result.append(SEPARATOR);
		if ((separators & IS_UNC) != 0)
			result.append(SEPARATOR);
		int len = segments.length;
		//append all segments with separators
		for (int i = 0; i < len; i++) {
			if (segments[i].indexOf(DEVICE_SEPARATOR) >= 0)
				encodeSegment(segments[i], result);
			else
				result.append(segments[i]);
			if (i < len - 1 || (separators & HAS_TRAILING) != 0)
				result.append(SEPARATOR);
		}
		return result.toString();
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#toString()
	 */
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		int resultSize = computeLength();
		if (resultSize <= 0)
			return EMPTY_STRING;
		char[] result = new char[resultSize];
		int offset = 0;
		if (device != null) {
			int size = device.length();
			device.getChars(0, size, result, offset);
			offset += size;
		}
		if ((separators & HAS_LEADING) != 0)
			result[offset++] = SEPARATOR;
		if ((separators & IS_UNC) != 0)
			result[offset++] = SEPARATOR;
		int len = segments.length - 1;
		if (len >= 0) {
			//append all but the last segment, with separators
			for (int i = 0; i < len; i++) {
				int size = segments[i].length();
				segments[i].getChars(0, size, result, offset);
				offset += size;
				result[offset++] = SEPARATOR;
			}
			//append the last segment
			int size = segments[len].length();
			segments[len].getChars(0, size, result, offset);
			offset += size;
		}
		if ((separators & HAS_TRAILING) != 0)
			result[offset++] = SEPARATOR;
		return new String(result);
	}

	/* (Intentionally not included in javadoc)
	 * @see Path#uptoSegment(int)
	 */
	/**
	 * <p>uptoSegment.</p>
	 *
	 * @param count a int.
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.Path} object.
	 */
	public Path uptoSegment(int count) {
		if (count == 0)
			return new Path(device, NO_SEGMENTS, separators & (HAS_LEADING | IS_UNC));
		if (count >= segments.length)
			return this;
		String[] newSegments = new String[count];
		System.arraycopy(segments, 0, newSegments, 0, count);
		return new Path(device, newSegments, separators);
	}
}
