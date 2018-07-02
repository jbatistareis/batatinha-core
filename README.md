# Batatinha core
This is (~~not a libretro core, don't ask~~) a Java CHIP-8/Superchip emulator library, made from the guts my other CHIP-8 emulator ([Batatinha](https://github.com/jbatistareis/batatinha)), created to be used on an Android port (([Batatinha v2](https://github.com/jbatistareis/batatinha-v2))).
It is based on the VIP and SCHIP 1.1 instruction sets, auto choosing which based on the instructions used. Some newer games are made to be run with Octo instruction set, so they might not run correctly.

Usage:
 1. Instantiate Chip8 class: `final Chip8 chip8 = new Chip8();`
 2. Call the `loadProgram(InputStream program)` method: `chip8.loadProgram(new FileInputStream(file));`
 3. Call the `step()` method for each cpu tick: `for(int i = 0, i < 8; i++){ chip8.step(); } //~500Hz @ 60fps`
 4. Get the display framebuffer from the `getDisplayBuffer()` method, it is an array composed by 0s and 1s, just draw it using your favorite method
 5. Input is processed using the methods `presKey(Key key)` and `releaseKey(Key key)`