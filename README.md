# KunpengIntellIJPlugin

The Kunpeng DevKit contains Porting Advisor, Hyper Tuner, and other tools. The IntelliJ plugin, functioning as the client of Porting Advisor and Hyper Tuner, works with the server to help developers accelerate application porting and computing power upgrade.
## Kunpeng Porting Advisor
The Kunpeng Porting Advisor is a tool that simplifies the porting of applications to the servers powered by Kunpeng 916 or Kunpeng 920 processors. This tool applies only to development and test environments and supports only scanning, analysis, and porting of software from x86 servers running Linux to Kunpeng servers running Linux.

- Software porting assessment: The tool scans and analyzes the x86 software to be ported, assesses the porting feasibility, and provides links for downloading dependency files compatible with the Kunpeng platform.
- Source code porting: The tool automatically scans and analyzes C, C++, Fortran, Python, and assembly source code, assesses the dependency files to be ported, provides code modification suggestions, and transcodes the x86 assembly instructions in the source code into equivalent Kunpeng assembly instructions. You can quickly modify the source code based on the suggestions, or apply a batch modification for the same type of code with one click.
- Software package rebuild: The tool analyzes the composition and dependency of the Linux software package on the x86 platform, replaces the x86 dependency files with those compatible with the Kunpeng platform, and rebuilds the software package applicable to the Kunpeng platform.
- Dedicated software porting: You can modify, compile, and rebuild the commonly used dedicated software applicable to the Kunpeng platform with a few clicks.
- Enhanced functions: The tool provides static checks for software code quality, including 64-bit mode compatibility check, structure byte alignment check, cache line alignment check, and weak memory ordering check.
## Kunpeng Hyper Tuner
The Kunpeng Hyper Tuner is a tool set that consists of the System Profiler, Java Profiler, System Diagnosis, and Tuning Assistant.
### System Profiler
The System Profiler is a performance analysis and tuning tool for Kunpeng 916 or 920 servers. It collects performance data of processors, operating system, processes, threads, and functions, analyzes system performance metrics, and locates bottlenecks and hotspot functions.
### Java Profiler
Java Profiler is a tool for analyzing and tuning the performance of Java programs running on TaiShan servers. It can analyze and optimize Java programs on local or remote servers, graphically display information about heaps, threads, locks, and garbage collection (GC) of Java programs, collect hotspot functions, and locate program bottlenecks, allowing you take dedicated measures for tuning.
### System Diagnosis
The tool analyzes system operating metrics to identify exceptions, such as memory leakage, memory overwriting, and network packet loss, and provides tuning suggestions. The tool also supports system pressure tests, such as the network I/O pressure test, which helps evaluate the system's maximum performance.
### Tuning Assistant
The Tuning Assistant systematically organizes and analyzes performance metrics, hotspot functions, and system configurations to form a system resource consumption chain. It provides guidance for analyzing performance bottlenecks based on tuning paths and gives tuning suggestions and operation guides for each tuning path to implement fast tuning.

# Build
To build the Hyper Tuner client, invoke ./tuning_build_webview.sh to build the webview, and then invoke ./build.sh hypertuner.
To build the Porting Advisor client, invoke ./build_webview.sh to build the webview, and then invoke ./build.sh portingadvisor.