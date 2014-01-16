ALife with a Befunge-like language

![FungeWorld](https://raw.github.com/gvellut/fungeworld/master/doc/screenshot.png)

Some ideas:
- Introduce energy (processes die when they run out) + transfer when spawning a child process
- Add operators related to energy (read, write) + a Mason grid that holds this energy 
- Introduce patterns to the energy grid. Can the process evolve to follow them?
- Add event: currently only active read. Introduce some events. 
Maybe have a notion of a body => whatever process last executed a cell owns it
- Refactor the board / simulation API: Move all mutations inside the interpreter
- Allow definition of multiple simulation parameters without changing source
- Different directions for the ancestor processes
- Remove remnants of async design
- Visualizer for genral infos (num of processes, stats...)

