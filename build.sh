#!/bin/bash
gradle convertCss
jbake -b
cp -rv output/* ./
