#!/usr/bin/env bash

version=${1:-"0.1.0"}
username=${2:-"Steven Tompkins"}
modpack=${3:-"ezstorage-test-env"}

cp "build/libs/s3-$version.jar" "/mnt/c/Users/${username}/AppData/Roaming/gdlauncher_carbon/data/instances/${modpack}/instance/mods"
