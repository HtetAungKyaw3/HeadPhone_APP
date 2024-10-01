import subprocess
import os
import time

GRADLEW_CLEAN = ["gradlew.bat", "clean", "--rerun-tasks"]
GRADLEW_BUILD_ALL = ["gradlew.bat", "assembleRelease", "--rerun-tasks"]
#GRADLEW_BUILD_ALL = ["gradlew.bat", "assembleRelease", "--rerun-tasks", "--gradle-user-home", r"C:\\Users\\tool\\.gradle\\"]
#GRADLEW_JAVADOC = ["C:\Program Files\Java\jdk1.8.0_101\bin\javadoc.exe", "javadoc"]

my_env = os.environ.copy()

def runGradlewClean():
    return subprocess.check_call(GRADLEW_CLEAN, env=my_env)

def runGradlewBuildAll():
    return subprocess.check_call(GRADLEW_BUILD_ALL, env=my_env)

#def runGradlewJavadoc():
#    return subprocess.check_call(GRADLEW_JAVADOC)

def clean_buildall_genDoc():
    print("runGradlewClean()")
    if(runGradlewClean() ==0):
        print("runGradlewBuildAll()")
        if(runGradlewBuildAll() ==0):
            print("Success in clean_buildall_genDoc()")
            return 0
#           if(runGradlewJavadoc() == 0):

    else:
        print("Failed in clean_buildall_genDoc()")
        return -1

if __name__ == '__main__':
    clean_buildall_genDoc()
