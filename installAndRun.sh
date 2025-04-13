if [ $# -ne 2 ]; then
    echo "example: ./installAndRun.sh doors1.json building_types.json"
    exit 1
fi
./gradlew clean installDist
./app/build/install/app/bin/app "$1" "$2"
#./app/build/install/app/bin/app -nw "$1" "$2"
