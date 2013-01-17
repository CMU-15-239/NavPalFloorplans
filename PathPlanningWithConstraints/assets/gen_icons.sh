#!/usr/bin/env bash
echo "Moving and converting icons to drawable resources"
cp ic_navigation.png ../res/drawable-hdpi/icon.png
convert ic_navigation.png -resize 36x36 ../res/drawable-ldpi/icon.png
convert ic_navigation.png -resize 48x48 ../res/drawable-mdpi/icon.png
