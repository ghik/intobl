import subprocess
import time

plot = subprocess.Popen(['gnuplot'], stdin=subprocess.PIPE)
plot.stdin.write("plot sin(x)\n")

time.sleep(1)

plot.stdin.close()

