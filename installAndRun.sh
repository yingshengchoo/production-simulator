if [ $# -ne 1 ]; then
    echo "example: ./installAndRun.sh doors1.json"
    exit 1
fi
./gradlew clean installDist
./app/build/install/app/bin/app "$1"