#include <iostream>
#include "sys/types.h"
#include <windows.h>
#include "JNICall.h"
static double CalculateCPULoad(unsigned long long idleTicks, unsigned long long totalTicks);
static unsigned long long FileTimeToInt64(const FILETIME & ft);
FILETIME prevSysIdle, prevSysKernel, prevSysUser;

JNIEXPORT jdouble JNICALL Java_JNICall_getMemoryUsage
  (JNIEnv *, jobject)
  {
	FILETIME idleTime, kernelTime, userTime;
		return GetSystemTimes(&idleTime, &kernelTime, &userTime) ? CalculateCPULoad(FileTimeToInt64(idleTime), FileTimeToInt64(kernelTime) + FileTimeToInt64(userTime)) : -1.0f;
	
}

static unsigned long long FileTimeToInt64(const FILETIME & ft)
{
	return (((unsigned long long)(ft.dwHighDateTime)) << 32) | ((unsigned long long)ft.dwLowDateTime);
}
static double CalculateCPULoad(unsigned long long idleTicks, unsigned long long totalTicks)
{
	static unsigned long long _previousTotalTicks = 0;
	static unsigned long long _previousIdleTicks = 0;

	unsigned long long totalTicksSinceLastTime = totalTicks - _previousTotalTicks;
	unsigned long long idleTicksSinceLastTime = idleTicks - _previousIdleTicks;


	double ret = 1.0f - ((totalTicksSinceLastTime > 0) ? ((float)idleTicksSinceLastTime) / totalTicksSinceLastTime : 0);

	_previousTotalTicks = totalTicks;
	_previousIdleTicks = idleTicks;
	return ret;
}
