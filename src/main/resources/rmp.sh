#!/bin/sh
#
#Starts RMP (/etc/init.d script for raspberry pi)

#first umount the sd card from default location and mount at /mnt/music
umount /dev/sda1
mount /dev/sda1 /mnt/music/

#then start the RMP service
/home/rmp/RMP-1.0/bin/RMP > /home/rmp/rmpRoot.out 2>&1
