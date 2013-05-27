#!/bin/bash
lein compile && drip -cp `lein classpath` hangtime.server.core
