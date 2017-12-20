#include "thinger/thinger.h"
#include "sys/types.h"
#include "sys/sysinfo.h"

#define USER_ID             "jurgen"
#define DEVICE_ID           "Acer_Laptop"
#define DEVICE_CREDENTIAL   "DB@qklnOU2FQ"

double getMemoryUse();

int main(int argc, char *argv[])
{
    thinger_device thing(USER_ID, DEVICE_ID, DEVICE_CREDENTIAL);

    thing["mem_usage"] >> [](pson& out){
        out = getMemoryUse();
    };

    thing["Command"] << [](pson& in){
        const char* cmd = (const char*)in;
        if(strcmp(cmd, "print_yes") == 0) std::cout << "+" << std::endl;
        else if(strcmp(cmd, "print_no") == 0) std::cout << "-" << std::endl;
    };

    thing.start();
    return 0;
}

double getMemoryUse()
{
    struct sysinfo mem_info;

    sysinfo(&mem_info);
    long long total_mem = mem_info.totalram;
    total_mem *= mem_info.mem_unit;
    long long used_mem = mem_info.totalram - mem_info.freeram;
    used_mem *= mem_info.mem_unit;

    return ((long double)used_mem/(long double)total_mem * 100.0);
}
