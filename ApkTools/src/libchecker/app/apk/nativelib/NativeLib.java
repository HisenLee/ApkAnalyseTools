
package libchecker.app.apk.nativelib;

import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.file.elf.Attribute;
import libchecker.app.util.file.elf.Elf;

import java.io.File;

public class NativeLib {
    private String name = "unknown.so";
    private String abi_reported = ABI.ABI_UNKNOWN;
    private String abi_actually = ABI.ABI_UNKNOWN;
    private String path = null;

    public NativeLib(String fullPath) {
        path = fullPath;
        name = fullPath.substring(fullPath.lastIndexOf(Env.slash()) + 1);
        if (fullPath.contains(Env.slash() + ABI.ABI_ARMV5 + Env.slash())) {
            abi_reported = ABI.ABI_ARMV5;
        } else if (fullPath.contains(Env.slash() + ABI.ABI_ARMV7 + Env.slash())) {
            abi_reported = ABI.ABI_ARMV7;
        } else if (fullPath.contains(Env.slash() + ABI.ABI_ARMV8 + Env.slash())) {
            abi_reported = ABI.ABI_ARMV8;
        }  else if (fullPath.contains(Env.slash() + ABI.ABI_X86 + Env.slash())) {
            abi_reported = ABI.ABI_X86;
        } else if (fullPath.contains(Env.slash() + ABI.ABI_X64 + Env.slash())) {
            abi_reported = ABI.ABI_X64;
        }
        Attribute attr = null;
        try {
            attr = Elf.getAttributes(new File(fullPath));
        } catch (Exception e) {
        	Log.i("Error getting ELF of: " + fullPath);
        }
        if (attr == null) {
            abi_actually = abi_reported;
            return;
        }
        String cpu = attr.getCPU();
        if (cpu.equals(ABI.ABI_X86)) {
            abi_actually = ABI.ABI_X86;
        } else if (cpu.equals("arm")) {
            abi_actually = ABI.ABI_ARMV5;
        } else if (cpu.equals(ABI.ABI_ARMV8)) {
            abi_actually = ABI.ABI_ARMV8;
        } else if (cpu.equals(ABI.ABI_X64)) {
            abi_actually = ABI.ABI_X64;
        } else if (cpu != null) {
            abi_actually = cpu;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isArm32() {
        return abi_actually.equals(ABI.ABI_ARMV5);
    }
    
    public boolean isArm64() {
        return abi_actually.equals(ABI.ABI_ARMV8);
    }

    public boolean isX86() {
        return abi_actually.equals(ABI.ABI_X86);
    }
    
    public boolean isX64() {
        return abi_actually.equals(ABI.ABI_X64);
    }

    // reported as x86, actually arm
    public boolean isMisuse() {
        return !abi_reported.equals(abi_actually);
    }

    public String shortPath() {
        int assIndex = path.indexOf(Env.slash() + "assets" + Env.slash());
        int libIndex = path.indexOf(Env.slash() + "lib" + Env.slash());
        if (assIndex >= 0) {
            return path.substring(assIndex + 1);
        } else if (libIndex >= 0) {
            return path.substring(libIndex + 1);
        } else {
            return path;
        }
    }

    public void dump() {
        Log.v(path);
        Log.v(abi_reported);
        Log.v(abi_actually);
        Log.v(name);
        Log.v(isMisuse() + "");
    }
}
