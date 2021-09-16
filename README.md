# KMine
A Minecraft clone made in Kotlin

## Development process
### Day 1
First contact with LibGDX and LWJGL ever, struggled to display a cube sharing the same texture and material but mapping a specific UV region on each Cube's face
![Day 1](./.github/day1_0.png)

### Day 2
Using the incredible [OpenSimplexNoise](https://gist.github.com/KdotJPG/b1270127455a94ac5d19) (The successor of Perlin noise) to generate a simple terrain
With an even simpler chunk system... The chunks doesn't know its surroundings, therefore adding unnecessary faces on the draw calls
![Day 2 - 0](./.github/day2_0.png)

For the rest of the second day I fought with Gradle and IntelliJ, out of the blue the project stopped building
The wonderful Kotlin-ready-template made by [maltaisn](https://github.com/maltaisn/kmine) helped to solve the issues
And updated the libraries as well

After all the fighting I changed the noise function to use the third dimension
![Day 2 - 1](./.github/day2_1.png)

### Day 3
First of all, had my introduction to OpenGL ES shaders.
I was hoping to create a fragment shader that would invert the screen texture for the crosshair texture. Just like the original game
But I ended up using OpenGL blending to achieve the effect
![OpenGL blending](./.github/opengl_blending.png)

Started experimenting with lighting as well, trying to make a day-night cycle
And added the "torch cube" lol
![Day 3 - 0](./github/day3_0.png)