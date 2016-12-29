cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -Wall")

add_library(opensl_audio SHARED
            audio_recode_play.cpp
            opensl_io.c
            opensl_example.c
            java_interface_wrap.cpp
            opensl_io2.c
            opensl_io3.c
             )

# Include libraries needed for opensl_audio lib
target_link_libraries(opensl_audio
                      log
                      OpenSLES)
