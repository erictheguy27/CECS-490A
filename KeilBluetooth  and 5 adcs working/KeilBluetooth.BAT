SET PATH=D:\Keil\ARM\ARMCC\bin\;C:\Program Files (x86)\Common Files\Intel\Shared Libraries\redist\intel64_win\compiler;C:\Program Files (x86)\Common Files\Oracle\Java\javapath;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files\Intel\WiFi\bin\;C:\Program Files\Common Files\Intel\WirelessCommon\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;D:\MATLAB\bin;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\PuTTY\;C:\Program Files\NVIDIA Corporation\NVIDIA NvDLISR;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files (x86)\Common Files\Intel\Shared Libraries\redist\intel64_win\compiler;C:\Program Files (x86)\Common Files\Oracle\Java\javapath;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files\Intel\WiFi\bin\;C:\Program Files\Common Files\Intel\WirelessCommon\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\NVIDIA Corporation\NVIDIA NvDLISR;C:\Program Files\PuTTY\;D:\MATLAB\bin;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files\Intel\Intel(R) Management Engine Components\DAL;C:\Users\Mr.Asian27\AppData\Local\Microsoft\WindowsApps;C:\Users\Mr.Asian27\adb;%USERPROFILE%\AppData\Local\Microsoft\WindowsApps;
SET CPU_TYPE=LM4F120H5QR
SET CPU_VENDOR=Texas Instruments
SET UV2_TARGET=KeilBluetooth
SET CPU_CLOCK=0x007A1200
"D:\Keil\ARM\ARMCC\bin\ArmCC" --Via ".\main.__i"
"D:\Keil\ARM\ARMCC\bin\ArmAsm" --Via ".\startup._ia"
"D:\Keil\ARM\ARMCC\bin\ArmCC" --Via ".\systick.__i"
"D:\Keil\ARM\ARMCC\bin\ArmCC" --Via ".\uart.__i"
"D:\Keil\ARM\ARMCC\bin\ArmCC" --Via ".\pll.__i"
"D:\Keil\ARM\ARMCC\bin\ArmLink" --Via ".\KeilBluetooth.lnp"
"D:\Keil\ARM\ARMCC\bin\fromelf.exe" ".\KeilBluetooth.axf" --i32combined --output ".\KeilBluetooth.hex"
