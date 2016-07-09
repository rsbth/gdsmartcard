package gdsmartcard.io.winscard;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;

/**
 * Maps the WinSCard Java method names to the actual native function names.
 * <p>
 * Each function in the WinSCard.dll that takes a string has an implementation
 * taking 'char' and a different implementation that takes 'wchar_t'. We use the
 * ASCII version, since it is unlikely for reader names to contain non-ASCII
 * characters.
 */
public class WinSCardFunctionMapper implements FunctionMapper {
    static final Set<String> asciiSuffixNames = Collections
	    .unmodifiableSet(new HashSet<String>(Arrays.asList("SCardListReaderGroups", "SCardListReaders",
		    "SCardGetStatusChange", "SCardConnect", "SCardStatus")));

    @Override
    public String getFunctionName(NativeLibrary library, Method method) {
	String name = method.getName();
	if (asciiSuffixNames.contains(name)) {
	    name += 'A';
	}
	return name;
    }
}