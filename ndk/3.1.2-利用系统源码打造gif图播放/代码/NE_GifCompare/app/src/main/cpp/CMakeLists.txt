
cmake_minimum_required(VERSION 3.4.1)

aux_source_directory(. src_files)

add_library(
        native-lib
        SHARED
        native-lib.cpp
        ${src_files}
)

target_link_libraries(
        native-lib
        jnigraphics
        log)