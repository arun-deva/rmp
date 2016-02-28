# Remote Music Player

Plays music from the specified music source. The music metadata is read and indexed to an embedded ES instance.

Currently we are using it on a raspberry pi connected to an audio receiver. Music source is a mounted USB drive.

Building: 
./gradlew clean distZip installDist

Running from installed distribution
cd build/install/RMP/bin
on *nix:
./RMP
on Windows:
.\RMP.bat

Using:
http://localhost:8888/rmp
First select a music source location
Then either search or play a random selection (Play tab)
