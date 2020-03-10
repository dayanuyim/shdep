Shdep
=====

[![License](https://img.shields.io/github/license/angr/angr.svg)](https://github.com/angr/angr/blob/master/LICENSE)

Shdep is an analysis tool to trace the **sh**ell script **dep**endency.

Installation
------------

1. Prerequisite

    Shdep leverages [shfmt](https://github.com/mvdan/sh)/v2.0 to parse the script syntax tree. A copy can be found in `tools` folder. Put it in one of  your path in $PATH.

2. Build Gradle Task

        ./gradlew shdep

Run Shdep
---------

0. Security Concerns

    Some commands in scripts could be run for anlaysis. For security concerns, it is recommanded to **run the tool in a docker container or a vitual machine**.

1. Execute JAR

        java -jar build/libs/shdep.jar sample/cramfs-root  # To unpack the sample file first.
2. Output

    ```
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/readbits
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/eraseflash
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/echo
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/cp
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/set_led -> /bin/statusled
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/set_led -> /var/run/statusled
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/set_led -> /bin/networkled
    /sbin/rc.init -> /sbin/rc.factorydefault -> /bin/set_led -> /bin/powerled
    ...
    ...
    ...
    ```

