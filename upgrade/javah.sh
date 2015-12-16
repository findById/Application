#!/bin/sh
export ProjectPath=$(cd "../$(dirname "$1")"; pwd)
export TargetClassName="org.cn.core.upgrade.Smart"
export SourceFile="${ProjectPath}/app/src/main/java"
export TargetPath="${ProjectPath}/upgrade/src/main/jni"
cd "${SourceFile}"
javah -d ${TargetPath} -classpath "${SourceFile}" "${TargetClassName}"
echo -d ${TargetPath} -classpath "${SourceFile}" "${TargetClassName}"